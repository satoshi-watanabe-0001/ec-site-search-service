package com.ecsite.search.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品検索リクエストDTO.
 *
 * <p>GET /api/v1/search/productsエンドポイントのクエリパラメータを表現するDTO。 Jakarta Bean Validationによる入力検証を実装。
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

  /** 検索キーワード（任意）. */
  private String keyword;

  /** カテゴリフィルタ（任意）. */
  private String category;

  /**
   * ページ番号（デフォルト: 1）.
   *
   * <p>最小値: 1
   */
  @Min(value = 1, message = "Page must be at least 1")
  @Builder.Default
  private Integer page = 1;

  /**
   * 1ページあたりのアイテム数（デフォルト: 20）.
   *
   * <p>最小値: 1、最大値: 100
   */
  @Min(value = 1, message = "Size must be at least 1")
  @Max(value = 100, message = "Size must not exceed 100")
  @Builder.Default
  private Integer size = 20;

  /**
   * ソート順（デフォルト: relevance）.
   *
   * <p>許可される値: relevance, price_asc, price_desc, rating, newest
   */
  @Pattern(
      regexp = "^(relevance|price_asc|price_desc|rating|newest)$",
      message = "Sort must be one of: relevance, price_asc, price_desc, rating, newest")
  @Builder.Default
  private String sort = "relevance";
}
