package com.ecsite.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ecsite.search.model.ProductDocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Elasticsearch操作サービス.
 *
 * <p>Elasticsearchとの直接的なやり取りを担当するサービスクラス。 検索クエリの構築、実行、結果の取得を行う。
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchService {

  private final ElasticsearchClient elasticsearchClient;

  /**
   * 商品を検索する.
   *
   * @param keyword 検索キーワード（任意）
   * @param category カテゴリフィルタ（任意）
   * @param page ページ番号（1から開始）
   * @param size 1ページあたりのアイテム数
   * @param sort ソート順
   * @return 検索結果
   * @throws IOException Elasticsearch通信エラー
   */
  public SearchResponse<ProductDocument> searchProducts(
      String keyword, String category, int page, int size, String sort) throws IOException {

    log.info(
        "Searching products: keyword={}, category={}, page={}, size={}, sort={}",
        keyword,
        category,
        page,
        size,
        sort);

    Query query = buildQuery(keyword, category);

    int from = (page - 1) * size;

    SearchRequest.Builder searchRequestBuilder =
        new SearchRequest.Builder().index("products").query(query).from(from).size(size);

    applySorting(searchRequestBuilder, sort);

    SearchRequest searchRequest = searchRequestBuilder.build();

    SearchResponse<ProductDocument> response =
        elasticsearchClient.search(searchRequest, ProductDocument.class);

    log.info("Search completed: totalHits={}", response.hits().total().value());

    return response;
  }

  /**
   * 検索クエリを構築する.
   *
   * @param keyword 検索キーワード
   * @param category カテゴリフィルタ
   * @return Query
   */
  private Query buildQuery(String keyword, String category) {
    BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

    if (keyword != null && !keyword.trim().isEmpty()) {
      MultiMatchQuery multiMatchQuery =
          new MultiMatchQuery.Builder()
              .query(keyword)
              .fields("name^3", "description^2") // 重み付け: name^3, description^2
              .build();

      boolQueryBuilder.must(new Query.Builder().multiMatch(multiMatchQuery).build());
    }

    if (category != null && !category.trim().isEmpty()) {
      TermQuery termQuery = new TermQuery.Builder().field("category").value(category).build();

      boolQueryBuilder.filter(new Query.Builder().term(termQuery).build());
    }

    if ((keyword == null || keyword.trim().isEmpty())
        && (category == null || category.trim().isEmpty())) {
      return new Query.Builder().matchAll(builder -> builder).build();
    }

    return new Query.Builder().bool(boolQueryBuilder.build()).build();
  }

  /**
   * ソート設定を適用する.
   *
   * @param searchRequestBuilder SearchRequest.Builder
   * @param sort ソート順
   */
  private void applySorting(SearchRequest.Builder searchRequestBuilder, String sort) {
    switch (sort) {
      case "price_asc":
        searchRequestBuilder.sort(s -> s.field(f -> f.field("price").order(SortOrder.Asc)));
        break;
      case "price_desc":
        searchRequestBuilder.sort(s -> s.field(f -> f.field("price").order(SortOrder.Desc)));
        break;
      case "rating":
        searchRequestBuilder.sort(s -> s.field(f -> f.field("rating").order(SortOrder.Desc)));
        break;
      case "newest":
        searchRequestBuilder.sort(s -> s.field(f -> f.field("createdAt").order(SortOrder.Desc)));
        break;
      case "relevance":
      default:
        searchRequestBuilder.sort(s -> s.score(score -> score.order(SortOrder.Desc)));
        break;
    }
  }

  /**
   * 検索結果からProductDocumentのリストを抽出する.
   *
   * @param response 検索レスポンス
   * @return ProductDocumentのリスト
   */
  public List<ProductDocument> extractProducts(SearchResponse<ProductDocument> response) {
    List<ProductDocument> products = new ArrayList<>();

    for (Hit<ProductDocument> hit : response.hits().hits()) {
      if (hit.source() != null) {
        products.add(hit.source());
      }
    }

    return products;
  }

  /**
   * 検索結果の総件数を取得する.
   *
   * @param response 検索レスポンス
   * @return 総件数
   */
  public long getTotalHits(SearchResponse<ProductDocument> response) {
    return response.hits().total().value();
  }
}
