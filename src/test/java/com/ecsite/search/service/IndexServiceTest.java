package com.ecsite.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.ecsite.search.model.ProductDocument;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IndexServiceTest {

  @Mock private ElasticsearchClient elasticsearchClient;

  @InjectMocks private IndexService indexService;

  private ProductDocument testProduct1;
  private ProductDocument testProduct2;

  @BeforeEach
  void setUp() {
    testProduct1 =
        ProductDocument.builder()
            .id("1")
            .name("Test Product 1")
            .description("Test Description 1")
            .category("Electronics")
            .price(1000.0)
            .rating(4.5)
            .createdAt(LocalDateTime.now())
            .build();

    testProduct2 =
        ProductDocument.builder()
            .id("2")
            .name("Test Product 2")
            .description("Test Description 2")
            .category("Electronics")
            .price(2000.0)
            .rating(4.0)
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  void bulkIndexProducts_WithValidProducts_ReturnsSuccessCount() throws IOException {
    List<ProductDocument> products = Arrays.asList(testProduct1, testProduct2);

    BulkResponse mockBulkResponse = createMockBulkResponse(2, 0);
    when(elasticsearchClient.bulk(any(BulkRequest.class))).thenReturn(mockBulkResponse);

    int result = indexService.bulkIndexProducts(products);

    assertEquals(2, result);
    verify(elasticsearchClient).bulk(any(BulkRequest.class));
  }

  @Test
  void bulkIndexProducts_WithEmptyList_ReturnsZero() throws IOException {
    List<ProductDocument> products = Collections.emptyList();

    int result = indexService.bulkIndexProducts(products);

    assertEquals(0, result);
  }

  @Test
  void bulkIndexProducts_WithNullList_ReturnsZero() throws IOException {
    int result = indexService.bulkIndexProducts(null);

    assertEquals(0, result);
  }

  @Test
  void bulkIndexProducts_WithPartialFailure_ReturnsSuccessCount() throws IOException {
    List<ProductDocument> products = Arrays.asList(testProduct1, testProduct2);

    BulkResponse mockBulkResponse = createMockBulkResponse(1, 1);
    when(elasticsearchClient.bulk(any(BulkRequest.class))).thenReturn(mockBulkResponse);

    int result = indexService.bulkIndexProducts(products);

    assertEquals(1, result);
    verify(elasticsearchClient).bulk(any(BulkRequest.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  void indexProduct_WithValidProduct_IndexesSuccessfully() throws IOException {
    IndexResponse mockIndexResponse = mock(IndexResponse.class);
    when(elasticsearchClient.index(any(java.util.function.Function.class)))
        .thenReturn(mockIndexResponse);

    indexService.indexProduct(testProduct1);

    verify(elasticsearchClient).index(any(java.util.function.Function.class));
  }

  private BulkResponse createMockBulkResponse(int successCount, int failureCount) {
    BulkResponse mockResponse = mock(BulkResponse.class);
    List<BulkResponseItem> items = new java.util.ArrayList<>();

    for (int i = 0; i < successCount; i++) {
      BulkResponseItem successItem = mock(BulkResponseItem.class);
      when(successItem.error()).thenReturn(null);
      when(successItem.id()).thenReturn(String.valueOf(i));
      items.add(successItem);
    }

    for (int i = 0; i < failureCount; i++) {
      BulkResponseItem failureItem = mock(BulkResponseItem.class);
      co.elastic.clients.elasticsearch._types.ErrorCause errorCause =
          mock(co.elastic.clients.elasticsearch._types.ErrorCause.class);
      when(errorCause.reason()).thenReturn("Test error");
      when(failureItem.error()).thenReturn(errorCause);
      when(failureItem.id()).thenReturn(String.valueOf(successCount + i));
      items.add(failureItem);
    }

    when(mockResponse.items()).thenReturn(items);
    return mockResponse;
  }
}
