package com.ecsite.search.controller;

import com.ecsite.search.dto.SearchData;
import com.ecsite.search.dto.SearchRequest;
import com.ecsite.search.dto.SearchResponse;
import com.ecsite.search.service.SearchService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 検索APIコントローラー.
 *
 * <p>商品検索APIのエンドポイントを提供するコントローラークラス。 auth-serviceのパターンに準拠した実装。
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

  private final SearchService searchService;

  /**
   * 商品を検索する.
   *
   * <p>GET /api/v1/search/products
   *
   * <p>クエリパラメータ:
   *
   * <ul>
   *   <li>keyword: 検索キーワード（任意）
   *   <li>category: カテゴリフィルタ（任意）
   *   <li>page: ページ番号（デフォルト: 1）
   *   <li>size: 1ページあたりのアイテム数（デフォルト: 20）
   *   <li>sort: ソート順（デフォルト: relevance）
   * </ul>
   *
   * @param request 検索リクエスト
   * @return 検索レスポンス
   * @throws IOException Elasticsearch通信エラー
   */
  @GetMapping("/products")
  public ResponseEntity<SearchResponse> searchProducts(@Valid @ModelAttribute SearchRequest request)
      throws IOException {

    log.info("Search request received: {}", request);

    SearchData searchData =
        searchService.searchProducts(
            request.getKeyword(),
            request.getCategory(),
            request.getPage(),
            request.getSize(),
            request.getSort());

    SearchResponse response = SearchResponse.builder().status("success").data(searchData).build();

    log.info(
        "Search completed successfully: totalItems={}", searchData.getPagination().getTotalItems());

    return ResponseEntity.ok(response);
  }
}
