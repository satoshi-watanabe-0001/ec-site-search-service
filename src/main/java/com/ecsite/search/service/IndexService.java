package com.ecsite.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.ecsite.search.model.ProductDocument;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * インデックス管理サービス.
 *
 * <p>Elasticsearchインデックスへの商品データの一括登録を担当するサービスクラス。 MVP版として、シンプルなバルクインデックス機能を提供。
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndexService {

  private final ElasticsearchClient elasticsearchClient;

  /**
   * 商品を一括インデックスする.
   *
   * @param products 商品ドキュメントのリスト
   * @return インデックスに成功した件数
   * @throws IOException Elasticsearch通信エラー
   */
  public int bulkIndexProducts(List<ProductDocument> products) throws IOException {
    if (products == null || products.isEmpty()) {
      log.warn("No products to index");
      return 0;
    }

    log.info("Starting bulk index operation for {} products", products.size());

    BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();

    for (ProductDocument product : products) {
      bulkRequestBuilder.operations(
          op -> op.index(idx -> idx.index("products").id(product.getId()).document(product)));
    }

    BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequestBuilder.build());

    int successCount = 0;
    int failureCount = 0;

    for (BulkResponseItem item : bulkResponse.items()) {
      if (item.error() != null) {
        log.error("Failed to index product {}: {}", item.id(), item.error().reason());
        failureCount++;
      } else {
        successCount++;
      }
    }

    log.info(
        "Bulk index completed: success={}, failure={}, total={}",
        successCount,
        failureCount,
        products.size());

    return successCount;
  }

  /**
   * 商品を個別にインデックスする.
   *
   * @param product 商品ドキュメント
   * @throws IOException Elasticsearch通信エラー
   */
  public void indexProduct(ProductDocument product) throws IOException {
    log.info("Indexing product: id={}, name={}", product.getId(), product.getName());

    elasticsearchClient.index(idx -> idx.index("products").id(product.getId()).document(product));

    log.info("Product indexed successfully: id={}", product.getId());
  }
}
