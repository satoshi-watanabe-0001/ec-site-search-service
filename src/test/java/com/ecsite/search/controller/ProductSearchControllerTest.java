package com.ecsite.search.controller;

import com.ecsite.search.dto.ProductDto;
import com.ecsite.search.dto.ProductSearchRequest;
import com.ecsite.search.dto.ProductSearchResponse;
import com.ecsite.search.service.ElasticsearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ProductSearchController Test.
 * 商品検索コントローラーテスト
 */
@WebMvcTest(ProductSearchController.class)
class ProductSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElasticsearchService elasticsearchService;

    private ProductSearchResponse mockResponse;

    @BeforeEach
    void setUp() {
        List<ProductDto> products = Arrays.asList(
                ProductDto.builder()
                        .id("1")
                        .name("テスト商品1")
                        .description("テスト説明1")
                        .price(1000)
                        .category("カテゴリ1")
                        .stock(10)
                        .imageUrl("http://example.com/image1.jpg")
                        .build(),
                ProductDto.builder()
                        .id("2")
                        .name("テスト商品2")
                        .description("テスト説明2")
                        .price(2000)
                        .category("カテゴリ2")
                        .stock(20)
                        .imageUrl("http://example.com/image2.jpg")
                        .build()
        );

        mockResponse = ProductSearchResponse.builder()
                .products(products)
                .totalCount(2L)
                .currentPage(0)
                .pageSize(20)
                .totalPages(1)
                .build();
    }

    @Test
    void searchProductsSuccess() throws Exception {
        when(elasticsearchService.searchProducts(any(ProductSearchRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "テスト"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products.length()").value(2))
                .andExpect(jsonPath("$.products[0].name").value("テスト商品1"))
                .andExpect(jsonPath("$.products[1].name").value("テスト商品2"))
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void searchProductsWithAllParameters() throws Exception {
        when(elasticsearchService.searchProducts(any(ProductSearchRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", "テスト")
                        .param("category", "電子機器")
                        .param("minPrice", "500")
                        .param("maxPrice", "1500")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.totalCount").value(2));
    }

    @Test
    void searchProductsWithoutQuery() throws Exception {
        when(elasticsearchService.searchProducts(any(ProductSearchRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/products/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray());
    }

    @Test
    void searchProductsInvalidPageNumber() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                        .param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchProductsInvalidSize() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchProductsSizeTooLarge() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchProductsInvalidMinPrice() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                        .param("minPrice", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchProductsInvalidMaxPrice() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                        .param("maxPrice", "-1"))
                .andExpect(status().isBadRequest());
    }
}
