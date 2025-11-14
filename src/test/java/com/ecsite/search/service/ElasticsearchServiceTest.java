package com.ecsite.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.ecsite.search.dto.ProductSearchRequest;
import com.ecsite.search.dto.ProductSearchResponse;
import com.ecsite.search.exception.SearchServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ElasticsearchService Test.
 * Elasticsearchサービステスト
 */
@ExtendWith(MockitoExtension.class)
class ElasticsearchServiceTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @InjectMocks
    private ElasticsearchService elasticsearchService;

    private ProductSearchRequest searchRequest;
    private SearchResponse<Map> mockSearchResponse;

    @BeforeEach
    void setUp() {
        searchRequest = ProductSearchRequest.builder()
                .q("テスト商品")
                .page(0)
                .size(20)
                .build();
    }

    @Test
    void searchProductsSuccess() throws IOException {
        List<Hit<Map>> hits = new ArrayList<>();
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", "テスト商品");
        productData.put("description", "テスト説明");
        productData.put("price", 1000);
        productData.put("category", "テストカテゴリ");
        productData.put("stock", 10);
        productData.put("imageUrl", "http://example.com/image.jpg");

        Hit<Map> hit = mock(Hit.class);
        when(hit.id()).thenReturn("1");
        when(hit.source()).thenReturn(productData);
        hits.add(hit);

        TotalHits totalHits = TotalHits.of(t -> t.value(1L).relation(TotalHitsRelation.Eq));
        HitsMetadata<Map> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(hits);
        when(hitsMetadata.total()).thenReturn(totalHits);

        mockSearchResponse = mock(SearchResponse.class);
        when(mockSearchResponse.hits()).thenReturn(hitsMetadata);

        when(elasticsearchClient.search(any(SearchRequest.class), eq(Map.class)))
                .thenReturn(mockSearchResponse);

        ProductSearchResponse response = elasticsearchService.searchProducts(searchRequest);

        assertNotNull(response);
        assertEquals(1, response.getProducts().size());
        assertEquals("テスト商品", response.getProducts().get(0).getName());
        assertEquals(1L, response.getTotalCount());
        assertEquals(0, response.getCurrentPage());
        assertEquals(20, response.getPageSize());
        assertEquals(1, response.getTotalPages());

        verify(elasticsearchClient, times(1))
                .search(any(SearchRequest.class), eq(Map.class));
    }

    @Test
    void searchProductsWithCategoryFilter() throws IOException {
        searchRequest.setCategory("電子機器");

        TotalHits totalHits = TotalHits.of(t -> t.value(0L).relation(TotalHitsRelation.Eq));
        HitsMetadata<Map> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(new ArrayList<>());
        when(hitsMetadata.total()).thenReturn(totalHits);

        mockSearchResponse = mock(SearchResponse.class);
        when(mockSearchResponse.hits()).thenReturn(hitsMetadata);

        when(elasticsearchClient.search(any(SearchRequest.class), eq(Map.class)))
                .thenReturn(mockSearchResponse);

        ProductSearchResponse response = elasticsearchService.searchProducts(searchRequest);

        assertNotNull(response);
        assertEquals(0, response.getProducts().size());
        assertEquals(0L, response.getTotalCount());

        verify(elasticsearchClient, times(1))
                .search(any(SearchRequest.class), eq(Map.class));
    }

    @Test
    void searchProductsWithPriceRange() throws IOException {
        searchRequest.setMinPrice(500);
        searchRequest.setMaxPrice(1500);

        TotalHits totalHits = TotalHits.of(t -> t.value(0L).relation(TotalHitsRelation.Eq));
        HitsMetadata<Map> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(new ArrayList<>());
        when(hitsMetadata.total()).thenReturn(totalHits);

        mockSearchResponse = mock(SearchResponse.class);
        when(mockSearchResponse.hits()).thenReturn(hitsMetadata);

        when(elasticsearchClient.search(any(SearchRequest.class), eq(Map.class)))
                .thenReturn(mockSearchResponse);

        ProductSearchResponse response = elasticsearchService.searchProducts(searchRequest);

        assertNotNull(response);
        assertEquals(0, response.getProducts().size());

        verify(elasticsearchClient, times(1))
                .search(any(SearchRequest.class), eq(Map.class));
    }

    @Test
    void searchProductsEmptyQuery() throws IOException {
        searchRequest.setQ(null);

        TotalHits totalHits = TotalHits.of(t -> t.value(0L).relation(TotalHitsRelation.Eq));
        HitsMetadata<Map> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(new ArrayList<>());
        when(hitsMetadata.total()).thenReturn(totalHits);

        mockSearchResponse = mock(SearchResponse.class);
        when(mockSearchResponse.hits()).thenReturn(hitsMetadata);

        when(elasticsearchClient.search(any(SearchRequest.class), eq(Map.class)))
                .thenReturn(mockSearchResponse);

        ProductSearchResponse response = elasticsearchService.searchProducts(searchRequest);

        assertNotNull(response);
        assertEquals(0, response.getProducts().size());

        verify(elasticsearchClient, times(1))
                .search(any(SearchRequest.class), eq(Map.class));
    }

    @Test
    void searchProductsIoException() throws IOException {
        when(elasticsearchClient.search(any(SearchRequest.class), eq(Map.class)))
                .thenThrow(new IOException("Connection error"));

        assertThrows(SearchServiceException.class, () -> {
            elasticsearchService.searchProducts(searchRequest);
        });

        verify(elasticsearchClient, times(1))
                .search(any(SearchRequest.class), eq(Map.class));
    }

    @Test
    void searchProductsPagination() throws IOException {
        searchRequest.setPage(1);
        searchRequest.setSize(10);

        TotalHits totalHits = TotalHits.of(t -> t.value(25L).relation(TotalHitsRelation.Eq));
        HitsMetadata<Map> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(new ArrayList<>());
        when(hitsMetadata.total()).thenReturn(totalHits);

        mockSearchResponse = mock(SearchResponse.class);
        when(mockSearchResponse.hits()).thenReturn(hitsMetadata);

        when(elasticsearchClient.search(any(SearchRequest.class), eq(Map.class)))
                .thenReturn(mockSearchResponse);

        ProductSearchResponse response = elasticsearchService.searchProducts(searchRequest);

        assertNotNull(response);
        assertEquals(25L, response.getTotalCount());
        assertEquals(1, response.getCurrentPage());
        assertEquals(10, response.getPageSize());
        assertEquals(3, response.getTotalPages());

        verify(elasticsearchClient, times(1))
                .search(any(SearchRequest.class), eq(Map.class));
    }
}
