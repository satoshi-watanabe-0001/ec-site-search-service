package com.ecsite.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.ecsite.search.model.ProductDocument;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
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
class ElasticsearchServiceTest {

  @Mock private ElasticsearchClient elasticsearchClient;

  @InjectMocks private ElasticsearchService elasticsearchService;

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
  void searchProducts_WithKeyword_ReturnsResults() throws IOException {
    SearchResponse<ProductDocument> mockResponse = createMockSearchResponse(2L);

    when(elasticsearchClient.search(any(SearchRequest.class), eq(ProductDocument.class)))
        .thenReturn(mockResponse);

    SearchResponse<ProductDocument> result =
        elasticsearchService.searchProducts("laptop", null, 1, 20, "relevance");

    assertNotNull(result);
    verify(elasticsearchClient).search(any(SearchRequest.class), eq(ProductDocument.class));
  }

  @Test
  void searchProducts_WithCategory_ReturnsResults() throws IOException {
    SearchResponse<ProductDocument> mockResponse = createMockSearchResponse(2L);

    when(elasticsearchClient.search(any(SearchRequest.class), eq(ProductDocument.class)))
        .thenReturn(mockResponse);

    SearchResponse<ProductDocument> result =
        elasticsearchService.searchProducts(null, "Electronics", 1, 20, "relevance");

    assertNotNull(result);
    verify(elasticsearchClient).search(any(SearchRequest.class), eq(ProductDocument.class));
  }

  @Test
  void searchProducts_WithPagination_ReturnsResults() throws IOException {
    SearchResponse<ProductDocument> mockResponse = createMockSearchResponse(100L);

    when(elasticsearchClient.search(any(SearchRequest.class), eq(ProductDocument.class)))
        .thenReturn(mockResponse);

    SearchResponse<ProductDocument> result =
        elasticsearchService.searchProducts("test", null, 2, 10, "relevance");

    assertNotNull(result);
    verify(elasticsearchClient).search(any(SearchRequest.class), eq(ProductDocument.class));
  }

  @Test
  void searchProducts_WithPriceAscSort_ReturnsResults() throws IOException {
    SearchResponse<ProductDocument> mockResponse = createMockSearchResponse(2L);

    when(elasticsearchClient.search(any(SearchRequest.class), eq(ProductDocument.class)))
        .thenReturn(mockResponse);

    SearchResponse<ProductDocument> result =
        elasticsearchService.searchProducts("test", null, 1, 20, "price_asc");

    assertNotNull(result);
    verify(elasticsearchClient).search(any(SearchRequest.class), eq(ProductDocument.class));
  }

  @Test
  void extractProducts_ReturnsProductList() {
    SearchResponse<ProductDocument> mockResponse = createMockSearchResponseWithProducts();

    List<ProductDocument> products = elasticsearchService.extractProducts(mockResponse);

    assertNotNull(products);
    assertEquals(2, products.size());
    assertEquals("1", products.get(0).getId());
    assertEquals("2", products.get(1).getId());
  }

  @Test
  void getTotalHits_ReturnsTotalCount() {
    SearchResponse<ProductDocument> mockResponse = createMockSearchResponse(100L);

    long totalHits = elasticsearchService.getTotalHits(mockResponse);

    assertEquals(100L, totalHits);
  }

  @SuppressWarnings("unchecked")
  private SearchResponse<ProductDocument> createMockSearchResponse(long totalHits) {
    SearchResponse<ProductDocument> mockResponse = mock(SearchResponse.class);
    HitsMetadata<ProductDocument> mockHitsMetadata = mock(HitsMetadata.class);
    TotalHits mockTotalHits = mock(TotalHits.class);

    when(mockResponse.hits()).thenReturn(mockHitsMetadata);
    when(mockHitsMetadata.total()).thenReturn(mockTotalHits);
    when(mockTotalHits.value()).thenReturn(totalHits);
    when(mockHitsMetadata.hits()).thenReturn(Arrays.asList());

    return mockResponse;
  }

  @SuppressWarnings("unchecked")
  private SearchResponse<ProductDocument> createMockSearchResponseWithProducts() {
    SearchResponse<ProductDocument> mockResponse = mock(SearchResponse.class);
    HitsMetadata<ProductDocument> mockHitsMetadata = mock(HitsMetadata.class);
    TotalHits mockTotalHits = mock(TotalHits.class);

    Hit<ProductDocument> hit1 = mock(Hit.class);
    Hit<ProductDocument> hit2 = mock(Hit.class);

    when(hit1.source()).thenReturn(testProduct1);
    when(hit2.source()).thenReturn(testProduct2);

    when(mockResponse.hits()).thenReturn(mockHitsMetadata);
    when(mockHitsMetadata.total()).thenReturn(mockTotalHits);
    when(mockTotalHits.value()).thenReturn(2L);
    when(mockHitsMetadata.hits()).thenReturn(Arrays.asList(hit1, hit2));

    return mockResponse;
  }
}
