package com.ecsite.search.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品検索レスポンスDTO.
 *
 * <p>GET /api/v1/search/productsエンドポイントのレスポンスを表現するDTO。 組織標準の統一レスポンス形式に準拠。
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

  /** ステータス（success/error）. */
  @Builder.Default private String status = "success";

  /** 検索データ（商品リストとページネーション情報）. */
  private SearchData data;

  /** レスポンス生成時刻. */
  @Builder.Default private LocalDateTime timestamp = LocalDateTime.now();

  /** リクエストID（トレーシング用）. */
  @Builder.Default private String requestId = UUID.randomUUID().toString();
}
