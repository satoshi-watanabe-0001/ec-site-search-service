package com.ecsite.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * EC Site Search Service Application.
 *
 * <p>Elasticsearch 8.xを使用した商品検索機能を提供するマイクロサービス。
 *
 * <p>主な機能:
 *
 * <ul>
 *   <li>商品のキーワード検索（日本語対応）
 *   <li>カテゴリフィルタリング
 *   <li>ページネーション対応
 *   <li>複数のソートオプション（関連度、価格、評価、日付）
 * </ul>
 *
 * @since 1.0.0
 */
@SpringBootApplication
public class SearchApplication {

  /**
   * アプリケーションのエントリーポイント.
   *
   * @param args コマンドライン引数
   */
  public static void main(String[] args) {
    SpringApplication.run(SearchApplication.class, args);
  }
}
