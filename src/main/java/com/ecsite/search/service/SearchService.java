package com.ecsite.search.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.ecsite.search.dto.PaginationInfo;
import com.ecsite.search.dto.ProductItem;
import com.ecsite.search.dto.SearchData;
import com.ecsite.search.model.ProductDocument;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 検索ビジネスロジックサービス.
 *
 * <p>商品検索のビジネスロジックを担当するサービスクラス。 ElasticsearchServiceを呼び出し、結果をDTOに変換する。
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

  private final ElasticsearchService elasticsearchService;

  /**
   * 商品を検索する.
   *
   * @param keyword 検索キーワード
   * @param category カテゴリフィルタ
   * @param page ページ番号
   * @param size 1ページあたりのアイテム数
   * @param sort ソート順
   * @return SearchData 検索データ
   * @throws IOException Elasticsearch通信エラー
   */
  public SearchData searchProducts(String keyword, String category, int page, int size, String sort)
      throws IOException {

    log.info(
        "SearchService.searchProducts called: keyword={}, category={}, page={}, size={}, sort={}",
        keyword,
        category,
        page,
        size,
        sort);

    SearchResponse<ProductDocument> response =
        elasticsearchService.searchProducts(keyword, category, page, size, sort);

    List<ProductDocument> productDocuments = elasticsearchService.extractProducts(response);

    List<ProductItem> productItems =
        productDocuments.stream().map(this::convertToProductItem).collect(Collectors.toList());

    long totalHits = elasticsearchService.getTotalHits(response);
    PaginationInfo paginationInfo = buildPaginationInfo(page, size, totalHits);

    return SearchData.builder().items(productItems).pagination(paginationInfo).build();
  }

  /**
   * ProductDocumentをProductItemに変換する.
   *
   * @param document ProductDocument
   * @return ProductItem
   */
  private ProductItem convertToProductItem(ProductDocument document) {
    return ProductItem.builder()
        .id(document.getId())
        .name(document.getName())
        .description(document.getDescription())
        .category(document.getCategory())
        .price(document.getPrice())
        .rating(document.getRating())
        .createdAt(document.getCreatedAt())
        .build();
  }

  /**
   * ページネーション情報を構築する.
   *
   * @param page 現在のページ番号
   * @param size 1ページあたりのアイテム数
   * @param totalHits 総件数
   * @return PaginationInfo
   */
  private PaginationInfo buildPaginationInfo(int page, int size, long totalHits) {
    int totalPages = (int) Math.ceil((double) totalHits / size);

    return PaginationInfo.builder()
        .currentPage(page)
        .totalPages(totalPages)
        .totalItems(totalHits)
        .hasNext(page < totalPages)
        .hasPrev(page > 1)
        .build();
  }
}
