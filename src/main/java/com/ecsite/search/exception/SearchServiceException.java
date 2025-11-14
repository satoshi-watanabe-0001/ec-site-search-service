package com.ecsite.search.exception;

/**
 * 検索サービス例外.
 *
 * <p>Elasticsearch連携やクエリ組み立ての失敗など、
 * 検索ドメインに関連するエラーを表します。</p>
 *
 * @since 1.0
 */
public class SearchServiceException extends RuntimeException {

    /**
     * エラーメッセージを指定して例外を生成する.
     *
     * @param message エラーメッセージ
     * @since 1.0
     */
    public SearchServiceException(String message) {
        super(message);
    }

    /**
     * エラーメッセージと原因例外を指定して例外を生成する.
     *
     * @param message エラーメッセージ
     * @param cause 原因例外
     * @since 1.0
     */
    public SearchServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
