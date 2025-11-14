package com.ecsite.search.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Elasticsearch商品ドキュメントモデル.
 *
 * <p>Elasticsearchの商品インデックスに格納される商品情報を表現するドキュメントクラス。 日本語テキスト検索に対応するため、kuromojiアナライザーを使用。
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products")
public class ProductDocument {

  /** 商品ID（UUID形式）. */
  @Id private String id;

  /**
   * 商品名.
   *
   * <p>検索時の重み付け: ^3（最も重要）
   */
  @Field(type = FieldType.Text, analyzer = "kuromoji")
  private String name;

  /**
   * 商品説明.
   *
   * <p>検索時の重み付け: ^2
   */
  @Field(type = FieldType.Text, analyzer = "kuromoji")
  private String description;

  /** カテゴリ名（完全一致検索用）. */
  @Field(type = FieldType.Keyword)
  private String category;

  /** 価格. */
  @Field(type = FieldType.Double)
  private Double price;

  /** 評価（0.0-5.0）. */
  @Field(type = FieldType.Double)
  private Double rating;

  /** 作成日時. */
  @Field(type = FieldType.Date)
  private LocalDateTime createdAt;

  /** 更新日時. */
  @Field(type = FieldType.Date)
  private LocalDateTime updatedAt;
}
