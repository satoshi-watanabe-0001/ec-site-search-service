package com.ecsite.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.ecsite.search.dto.PaginationInfo;
import com.ecsite.search.dto.SearchData;
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

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

  @Mock private ElasticsearchService elasticsearchService;

  @InjectMocks private SearchService searchService;

  private ProductDocument testProduct1;
  private ProductDocument testProduct2;
  private SearchResponse<ProductDocument> mockSearchResponse;

  @BeforeEach
  void setUp() {
    testProduct1 =
        ProductDocument.builder()
            .id("product-1")
            .name("Test Product 1")
            .description("Test Description 1")
            .category("Electronics")
            .price(1000.0)
            .rating(4.5)
            .createdAt(LocalDateTime.now())
            .build();

    testProduct2 =
        ProductDocument.builder()
            .id("product-2")
            .name("Test Product 2")
            .description("Test Description 2")
            .category("Books")
            .price(2000.0)
            .rating(4.0)
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  void searchProducts_WithKeyword_ReturnsSearchData() throws IOException {
    String keyword = "test";
    String category = null;
    int page = 1;
    int size = 20;
    String sort = "relevance";

    List<ProductDocument> products = Arrays.asList(testProduct1, testProduct2);

    when(elasticsearchService.searchProducts(keyword, category, page, size, sort))
        .thenReturn(mockSearchResponse);
    when(elasticsearchService.extractProducts(mockSearchResponse)).thenReturn(products);
    when(elasticsearchService.getTotalHits(mockSearchResponse)).thenReturn(2L);

    SearchData result = searchService.searchProducts(keyword, category, page, size, sort);

    assertNotNull(result);
    assertEquals(2, result.getItems().size());
    assertEquals("product-1", result.getItems().get(0).getId());
    assertEquals("Test Product 1", result.getItems().get(0).getName());

    PaginationInfo pagination = result.getPagination();
    assertEquals(1, pagination.getCurrentPage());
    assertEquals(1, pagination.getTotalPages());
    assertEquals(2L, pagination.getTotalItems());
    assertFalse(pagination.getHasNext());
    assertFalse(pagination.getHasPrev());

    verify(elasticsearchService).searchProducts(keyword, category, page, size, sort);
    verify(elasticsearchService).extractProducts(mockSearchResponse);
    verify(elasticsearchService).getTotalHits(mockSearchResponse);
  }

  @Test
  void searchProducts_WithCategory_ReturnsFilteredData() throws IOException {
    String keyword = null;
    String category = "Electronics";
    int page = 1;
    int size = 20;
    String sort = "price_asc";

    List<ProductDocument> products = Arrays.asList(testProduct1);

    when(elasticsearchService.searchProducts(keyword, category, page, size, sort))
        .thenReturn(mockSearchResponse);
    when(elasticsearchService.extractProducts(mockSearchResponse)).thenReturn(products);
    when(elasticsearchService.getTotalHits(mockSearchResponse)).thenReturn(1L);

    SearchData result = searchService.searchProducts(keyword, category, page, size, sort);

    assertNotNull(result);
    assertEquals(1, result.getItems().size());
    assertEquals("Electronics", result.getItems().get(0).getCategory());

    verify(elasticsearchService).searchProducts(keyword, category, page, size, sort);
  }

  @Test
  void searchProducts_WithPagination_ReturnsCorrectPaginationInfo() throws IOException {
    String keyword = "test";
    String category = null;
    int page = 2;
    int size = 10;
    String sort = "relevance";

    List<ProductDocument> products = Arrays.asList(testProduct1);

    when(elasticsearchService.searchProducts(keyword, category, page, size, sort))
        .thenReturn(mockSearchResponse);
    when(elasticsearchService.extractProducts(mockSearchResponse)).thenReturn(products);
    when(elasticsearchService.getTotalHits(mockSearchResponse)).thenReturn(25L);

    SearchData result = searchService.searchProducts(keyword, category, page, size, sort);

    PaginationInfo pagination = result.getPagination();
    assertEquals(2, pagination.getCurrentPage());
    assertEquals(3, pagination.getTotalPages());
    assertEquals(25L, pagination.getTotalItems());
    assertTrue(pagination.getHasNext());
    assertTrue(pagination.getHasPrev());

    verify(elasticsearchService).searchProducts(keyword, category, page, size, sort);
  }

  @Test
  void searchProducts_EmptyResults_ReturnsEmptyData() throws IOException {
    String keyword = "nonexistent";
    String category = null;
    int page = 1;
    int size = 20;
    String sort = "relevance";

    List<ProductDocument> products = Arrays.asList();

    when(elasticsearchService.searchProducts(keyword, category, page, size, sort))
        .thenReturn(mockSearchResponse);
    when(elasticsearchService.extractProducts(mockSearchResponse)).thenReturn(products);
    when(elasticsearchService.getTotalHits(mockSearchResponse)).thenReturn(0L);

    SearchData result = searchService.searchProducts(keyword, category, page, size, sort);

    assertNotNull(result);
    assertEquals(0, result.getItems().size());
    assertEquals(0, result.getPagination().getTotalPages());
    assertEquals(0L, result.getPagination().getTotalItems());

    verify(elasticsearchService).searchProducts(keyword, category, page, size, sort);
  }

  @Test
  void searchProducts_ThrowsIOException_PropagatesException() throws IOException {
    String keyword = "test";
    String category = null;
    int page = 1;
    int size = 20;
    String sort = "relevance";

    when(elasticsearchService.searchProducts(keyword, category, page, size, sort))
        .thenThrow(new IOException("Elasticsearch connection failed"));

    assertThrows(
        IOException.class, () -> searchService.searchProducts(keyword, category, page, size, sort));

    verify(elasticsearchService).searchProducts(keyword, category, page, size, sort);
  }
}
