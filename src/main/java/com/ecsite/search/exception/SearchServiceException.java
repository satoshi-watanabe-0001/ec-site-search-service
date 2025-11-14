package com.ecsite.search.exception;

/**
 * 検索サービス例外.
 * Search service exception
 */
public class SearchServiceException extends RuntimeException {

    public SearchServiceException(String message) {
        super(message);
    }

    public SearchServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
