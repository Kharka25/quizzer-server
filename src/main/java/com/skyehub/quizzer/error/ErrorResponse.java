package com.skyehub.quizzer.error;

import java.util.Date;
import java.util.List;

public record ErrorResponse(List<FieldErrorResponse> errors, String message, Long timestamp, String url) {
    public ErrorResponse(String message, List<FieldErrorResponse> fieldErrors, String url) {
        this(fieldErrors, message, new Date().getTime(), url);
    }

    public static ErrorResponse of(String message, List<FieldErrorResponse> errors, String url) {
        return new ErrorResponse(message, errors, url);
    }
}
