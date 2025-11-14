package com.ecsite.search.controller;

import com.ecsite.search.dto.ProductSearchRequest;
import com.ecsite.search.dto.ProductSearchResponse;
import com.ecsite.search.service.ElasticsearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品検索コントローラー.
 * Product search controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductSearchController {

    private final ElasticsearchService elasticsearchService;

    /**
     * 商品を検索する.
     * Search products
     *
     * @param request 検索リクエスト
     * @return 検索結果
     */
    @GetMapping("/search")
    public ResponseEntity<ProductSearchResponse> searchProducts(
            @Valid @ModelAttribute ProductSearchRequest request) {
        log.info("Received search request: q={}, category={}, minPrice={}, maxPrice={}, page={}, size={}",
                request.getQ(), request.getCategory(), request.getMinPrice(),
                request.getMaxPrice(), request.getPage(), request.getSize());

        ProductSearchResponse response = elasticsearchService.searchProducts(request);

        log.info("Returning search response: totalCount={}, currentPage={}, totalPages={}",
                response.getTotalCount(), response.getCurrentPage(), response.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
