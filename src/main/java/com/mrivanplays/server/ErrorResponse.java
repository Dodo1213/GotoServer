package com.mrivanplays.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private String timestamp;
    private String errorType;
    private int errorCode;
    private String message;
}
