package com.ecsite.search.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecsite.search.dto.PaginationInfo;
import com.ecsite.search.dto.ProductItem;
import com.ecsite.search.dto.SearchData;
import com.ecsite.search.service.SearchService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SearchService searchService;

  private SearchData testSearchData;

  @BeforeEach
  void setUp() {
    ProductItem item1 =
        ProductItem.builder()
            .id("product-1")
            .name("Test Product 1")
            .description("Test Description 1")
            .category("Electronics")
            .price(1000.0)
            .rating(4.5)
            .createdAt(LocalDateTime.now())
            .build();

    ProductItem item2 =
        ProductItem.builder()
            .id("product-2")
            .name("Test Product 2")
            .description("Test Description 2")
            .category("Books")
            .price(2000.0)
            .rating(4.0)
            .createdAt(LocalDateTime.now())
            .build();

    List<ProductItem> items = Arrays.asList(item1, item2);

    PaginationInfo pagination =
        PaginationInfo.builder()
            .currentPage(1)
            .totalPages(1)
            .totalItems(2L)
            .hasNext(false)
            .hasPrev(false)
            .build();

    testSearchData = SearchData.builder().items(items).pagination(pagination).build();
  }

  @Test
  @WithMockUser
  void searchProducts_WithKeyword_ReturnsSuccessResponse() throws Exception {
    when(searchService.searchProducts(eq("laptop"), isNull(), eq(1), eq(20), eq("relevance")))
        .thenReturn(testSearchData);

    mockMvc
        .perform(get("/api/v1/search/products").param("keyword", "laptop"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.data.items").isArray())
        .andExpect(jsonPath("$.data.items.length()").value(2))
        .andExpect(jsonPath("$.data.items[0].id").value("product-1"))
        .andExpect(jsonPath("$.data.items[0].name").value("Test Product 1"))
        .andExpect(jsonPath("$.data.pagination.currentPage").value(1))
        .andExpect(jsonPath("$.data.pagination.totalItems").value(2))
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.requestId").exists());

    verify(searchService).searchProducts("laptop", null, 1, 20, "relevance");
  }

  @Test
  @WithMockUser
  void searchProducts_WithCategory_ReturnsFilteredResults() throws Exception {
    when(searchService.searchProducts(isNull(), eq("Electronics"), eq(1), eq(20), eq("relevance")))
        .thenReturn(testSearchData);

    mockMvc
        .perform(get("/api/v1/search/products").param("category", "Electronics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.data.items").isArray());

    verify(searchService).searchProducts(null, "Electronics", 1, 20, "relevance");
  }

  @Test
  @WithMockUser
  void searchProducts_WithPagination_ReturnsCorrectPage() throws Exception {
    when(searchService.searchProducts(eq("test"), isNull(), eq(2), eq(10), eq("relevance")))
        .thenReturn(testSearchData);

    mockMvc
        .perform(
            get("/api/v1/search/products")
                .param("keyword", "test")
                .param("page", "2")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"));

    verify(searchService).searchProducts("test", null, 2, 10, "relevance");
  }

  @Test
  @WithMockUser
  void searchProducts_WithSort_ReturnsCorrectlySortedResults() throws Exception {
    when(searchService.searchProducts(eq("test"), isNull(), eq(1), eq(20), eq("price_asc")))
        .thenReturn(testSearchData);

    mockMvc
        .perform(get("/api/v1/search/products").param("keyword", "test").param("sort", "price_asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"));

    verify(searchService).searchProducts("test", null, 1, 20, "price_asc");
  }

  @Test
  @WithMockUser
  void searchProducts_WithInvalidPage_ReturnsBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/v1/search/products").param("page", "0"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value("error"))
        .andExpect(jsonPath("$.message").value("Validation failed"));

    verify(searchService, never()).searchProducts(any(), any(), anyInt(), anyInt(), any());
  }

  @Test
  @WithMockUser
  void searchProducts_WithInvalidSize_ReturnsBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/v1/search/products").param("size", "101"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value("error"))
        .andExpect(jsonPath("$.message").value("Validation failed"));

    verify(searchService, never()).searchProducts(any(), any(), anyInt(), anyInt(), any());
  }

  @Test
  @WithMockUser
  void searchProducts_WithInvalidSort_ReturnsBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/v1/search/products").param("sort", "invalid_sort"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value("error"))
        .andExpect(jsonPath("$.message").value("Validation failed"));

    verify(searchService, never()).searchProducts(any(), any(), anyInt(), anyInt(), any());
  }

  @Test
  @WithMockUser
  void searchProducts_ElasticsearchError_ReturnsServiceUnavailable() throws Exception {
    when(searchService.searchProducts(any(), any(), anyInt(), anyInt(), any()))
        .thenThrow(new IOException("Elasticsearch connection failed"));

    mockMvc
        .perform(get("/api/v1/search/products").param("keyword", "test"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.status").value("error"))
        .andExpect(jsonPath("$.message").value("Search service temporarily unavailable"));

    verify(searchService).searchProducts("test", null, 1, 20, "relevance");
  }

  @Test
  @WithMockUser
  void searchProducts_NoParameters_ReturnsAllProducts() throws Exception {
    when(searchService.searchProducts(isNull(), isNull(), eq(1), eq(20), eq("relevance")))
        .thenReturn(testSearchData);

    mockMvc
        .perform(get("/api/v1/search/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.data.items").isArray());

    verify(searchService).searchProducts(null, null, 1, 20, "relevance");
  }
}
