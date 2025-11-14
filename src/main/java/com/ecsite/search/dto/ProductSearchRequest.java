package com.ecsite.search.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品検索リクエストDTO.
 *
 * <p>製品検索APIのリクエストパラメータを保持します。
 * 検索クエリ、フィルタ条件、ページネーション情報を含みます。</p>
 *
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {

    /**
     * 検索クエリ（商品名・説明文）.
     * Search query for product name and description
     */
    private String q;

    /**
     * カテゴリフィルター.
     * Category filter
     */
    private String category;

    /**
     * 最小価格フィルター.
     * Minimum price filter
     */
    @Min(value = 0, message = "Minimum price must be greater than or equal to 0")
    private Integer minPrice;

    /**
     * 最大価格フィルター.
     * Maximum price filter
     */
    @Min(value = 0, message = "Maximum price must be greater than or equal to 0")
    private Integer maxPrice;

    /**
     * ページ番号（0始まり）.
     * Page number (0-indexed)
     */
    @Min(value = 0, message = "Page must be greater than or equal to 0")
    @Builder.Default
    private Integer page = 0;

    /**
     * ページサイズ.
     * Page size
     */
    @Min(value = 1, message = "Size must be greater than or equal to 1")
    @Max(value = 100, message = "Size must be less than or equal to 100")
    @Builder.Default
    private Integer size = 20;
}
