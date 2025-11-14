package com.ecsite.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ページネーション情報DTO.
 *
 * <p>検索結果のページネーション情報を表現するDTO。
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationInfo {

  /** 現在のページ番号. */
  private Integer currentPage;

  /** 総ページ数. */
  private Integer totalPages;

  /** 総アイテム数. */
  private Long totalItems;

  /** 次のページが存在するか. */
  private Boolean hasNext;

  /** 前のページが存在するか. */
  private Boolean hasPrev;
}
