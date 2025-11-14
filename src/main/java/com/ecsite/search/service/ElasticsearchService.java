package com.ecsite.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ecsite.search.dto.ProductDto;
import com.ecsite.search.dto.ProductSearchRequest;
import com.ecsite.search.dto.ProductSearchResponse;
import com.ecsite.search.exception.SearchServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Elasticsearch検索サービス.
 *
 * <p>Elasticsearch 8.xを使用した製品検索のビジネスロジックを提供します。
 * クエリ構築、検索実行、結果マッピングを担当します。</p>
 *
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {

    private static final String PRODUCT_INDEX = "products";
    private final ElasticsearchClient elasticsearchClient;

    /**
     * 商品を検索する.
     *
     * <p>検索クエリ、カテゴリ、価格範囲、ページネーション情報に基づいて
     * Elasticsearchから製品を検索します。</p>
     *
     * @param request 検索リクエスト
     * @return 検索結果とページネーション情報
     * @since 1.0
     */
    public ProductSearchResponse searchProducts(ProductSearchRequest request) {
        try {
            log.info("Searching products with query: q={}, category={}, minPrice={}, maxPrice={}, page={}, size={}",
                    request.getQ(), request.getCategory(), request.getMinPrice(),
                    request.getMaxPrice(), request.getPage(), request.getSize());

            Query query = buildQuery(request);

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(PRODUCT_INDEX)
                    .query(query)
                    .from(request.getPage() * request.getSize())
                    .size(request.getSize())
            );

            SearchResponse<Map> response = elasticsearchClient.search(
                    searchRequest,
                    Map.class
            );

            List<ProductDto> products = response.hits().hits().stream()
                    .map(this::mapHitToProduct)
                    .collect(Collectors.toList());

            long totalCount = response.hits().total() != null
                    ? response.hits().total().value()
                    : 0L;

            int totalPages = (int) Math.ceil((double) totalCount / request.getSize());

            log.info("Search completed: found {} products, total count: {}",
                    products.size(), totalCount);

            return ProductSearchResponse.builder()
                    .products(products)
                    .totalCount(totalCount)
                    .currentPage(request.getPage())
                    .pageSize(request.getSize())
                    .totalPages(totalPages)
                    .build();

        } catch (IOException e) {
            log.error("Error searching products: {}", e.getMessage(), e);
            throw new SearchServiceException("Failed to search products", e);
        }
    }

    /**
     * 検索クエリを構築する.
     * Build search query
     *
     * @param request 検索リクエスト
     * @return Query
     */
    private Query buildQuery(ProductSearchRequest request) {
        List<Query> mustQueries = new ArrayList<>();

        if (request.getQ() != null && !request.getQ().isEmpty()) {
            Query multiMatchQuery = Query.of(q -> q
                    .multiMatch(m -> m
                            .query(request.getQ())
                            .fields("name^2", "description")
                            .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                            .analyzer("kuromoji")
                    )
            );
            mustQueries.add(multiMatchQuery);
        }

        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            Query categoryQuery = Query.of(q -> q
                    .term(t -> t
                            .field("category.keyword")
                            .value(request.getCategory())
                    )
            );
            mustQueries.add(categoryQuery);
        }

        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            Query priceQuery = Query.of(q -> q
                    .range(r -> {
                        var rangeQuery = r.field("price");
                        if (request.getMinPrice() != null) {
                            rangeQuery.gte(co.elastic.clients.json.JsonData.of(request.getMinPrice()));
                        }
                        if (request.getMaxPrice() != null) {
                            rangeQuery.lte(co.elastic.clients.json.JsonData.of(request.getMaxPrice()));
                        }
                        return rangeQuery;
                    })
            );
            mustQueries.add(priceQuery);
        }

        if (mustQueries.isEmpty()) {
            return Query.of(q -> q.matchAll(m -> m));
        }

        BoolQuery boolQuery = BoolQuery.of(b -> b.must(mustQueries));
        return Query.of(q -> q.bool(boolQuery));
    }

    /**
     * Elasticsearch HitをProductDtoにマッピングする.
     * Map Elasticsearch Hit to ProductDto
     *
     * @param hit Elasticsearch Hit
     * @return ProductDto
     */
    private ProductDto mapHitToProduct(Hit<Map> hit) {
        Map<String, Object> source = hit.source();
        if (source == null) {
            return ProductDto.builder().build();
        }

        return ProductDto.builder()
                .id(hit.id())
                .name(getStringValue(source, "name"))
                .description(getStringValue(source, "description"))
                .price(getIntegerValue(source, "price"))
                .category(getStringValue(source, "category"))
                .stock(getIntegerValue(source, "stock"))
                .imageUrl(getStringValue(source, "imageUrl"))
                .build();
    }

    /**
     * Mapから文字列値を取得する.
     * Get string value from Map
     *
     * @param map Map
     * @param key キー
     * @return 文字列値
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Mapから整数値を取得する.
     * Get integer value from Map
     *
     * @param map Map
     * @param key キー
     * @return 整数値
     */
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
