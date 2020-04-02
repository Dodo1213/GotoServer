package com.mrivanplays.server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class PostErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PostError.class)
    public ResponseEntity<ErrorResponse> postError(PostError ex, WebRequest webRequest) {
        return new ResponseEntity<>(ex.toErrorResponse(), ex.getStatus());
    }
}
