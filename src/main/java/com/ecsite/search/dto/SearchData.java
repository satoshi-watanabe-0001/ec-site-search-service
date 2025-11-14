package com.ecsite.search.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 検索データDTO.
 *
 * <p>検索結果のデータ部分（商品リストとページネーション情報）を表現するDTO。
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchData {

  /** 商品アイテムリスト. */
  private List<ProductItem> items;

  /** ページネーション情報. */
  private PaginationInfo pagination;
}
