package com.ecsite.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品検索レスポンスDTO.
 *
 * <p>製品検索APIのレスポンスデータを保持します。
 * 検索結果の製品リストとページネーション情報を含みます。</p>
 *
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponse {

    /**
     * 検索結果の商品リスト.
     * List of products in search results
     */
    private List<ProductDto> products;

    /**
     * 総件数.
     * Total count
     */
    private Long totalCount;

    /**
     * 現在のページ番号.
     * Current page number
     */
    private Integer currentPage;

    /**
     * ページサイズ.
     * Page size
     */
    private Integer pageSize;

    /**
     * 総ページ数.
     * Total pages
     */
    private Integer totalPages;
}
