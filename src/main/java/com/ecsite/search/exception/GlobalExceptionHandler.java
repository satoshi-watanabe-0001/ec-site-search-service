package com.ecsite.search.exception;

import com.ecsite.search.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * グローバル例外ハンドラー.
 *
 * <p>アプリケーション全体の例外を捕捉し、
 * 統一されたエラーレスポンスを生成します。</p>
 *
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * バリデーションエラーハンドラー.
     *
     * <p>Bean Validationによる入力検証エラーを処理し、
     * フィールドごとのエラー情報を含むレスポンスを返します。</p>
     *
     * @param ex バリデーション例外
     * @return エラーレスポンス（HTTP 400）
     * @since 1.0
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        log.error("Validation error occurred: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status("error")
                .message("Validation failed")
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 検索サービス例外ハンドラー.
     *
     * <p>検索処理中に発生したドメイン例外を処理し、
     * エラーレスポンスを返します。</p>
     *
     * @param ex 検索サービス例外
     * @return エラーレスポンス（HTTP 500）
     * @since 1.0
     */
    @ExceptionHandler(SearchServiceException.class)
    public ResponseEntity<ErrorResponse> handleSearchServiceException(
            SearchServiceException ex) {
        log.error("Search service error occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status("error")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    /**
     * 一般例外ハンドラー.
     *
     * <p>予期しない例外を捕捉し、
     * 汎用的なエラーレスポンスを返します。</p>
     *
     * @param ex 例外
     * @return エラーレスポンス（HTTP 500）
     * @since 1.0
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status("error")
                .message("An unexpected error occurred")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
