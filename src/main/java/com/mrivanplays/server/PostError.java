package com.mrivanplays.server;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.Instant;

public class PostError extends RuntimeException {

    private int code;
    private Instant timestamp;

    public PostError(String message, int code) {
        super(message);
        this.code = code;
        this.timestamp = Instant.now();
    }

    public HttpStatus getStatus() {
        return HttpStatus.valueOf(code);
    }

    public ErrorResponse toErrorResponse() {
        return ErrorResponse.builder()
                .errorType(getStatus().getReasonPhrase().replace(" ", ""))
                .errorCode(code)
                .message(getMessage())
                .timestamp(Timestamp.from(timestamp).toString())
                .build();
    }
}
