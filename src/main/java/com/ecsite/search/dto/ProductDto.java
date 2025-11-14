package com.ecsite.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品DTO.
 *
 * <p>製品情報を転送するためのデータ転送オブジェクトです。
 * API応答やレイヤー間のデータ転送に使用します。</p>
 *
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    /**
     * 商品ID.
     * Product ID
     */
    private String id;

    /**
     * 商品名.
     * Product name
     */
    private String name;

    /**
     * 商品説明.
     * Product description
     */
    private String description;

    /**
     * 価格.
     * Price
     */
    private Integer price;

    /**
     * カテゴリ.
     * Category
     */
    private String category;

    /**
     * 在庫数.
     * Stock quantity
     */
    private Integer stock;

    /**
     * 画像URL.
     * Image URL
     */
    private String imageUrl;
}
