package com.ecsite.search.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品アイテムDTO.
 *
 * <p>検索結果に含まれる個別の商品情報を表現するDTO。
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItem {

  /** 商品ID. */
  private String id;

  /** 商品名. */
  private String name;

  /** 商品説明. */
  private String description;

  /** カテゴリ名. */
  private String category;

  /** 価格. */
  private Double price;

  /** 評価. */
  private Double rating;

  /** 作成日時. */
  private LocalDateTime createdAt;
}
