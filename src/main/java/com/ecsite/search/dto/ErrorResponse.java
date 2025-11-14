package com.ecsite.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * エラーレスポンスDTO.
 *
 * <p>APIエラー時のレスポンスデータを保持します。
 * エラーメッセージ、フィールドエラー、タイムスタンプを含みます。</p>
 *
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * ステータス.
     * Status
     */
    private String status;

    /**
     * エラーメッセージ.
     * Error message
     */
    private String message;

    /**
     * フィールドエラーリスト.
     * List of field errors
     */
    private List<FieldError> errors;

    /**
     * タイムスタンプ.
     * Timestamp
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * フィールドエラー.
     *
     * <p>バリデーションエラーの詳細情報を保持します。
     * フィールド名とエラーメッセージを含みます。</p>
     *
     * @since 1.0
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        /**
         * フィールド名.
         * Field name
         */
        private String field;

        /**
         * エラーメッセージ.
         * Error message
         */
        private String message;
    }
}
