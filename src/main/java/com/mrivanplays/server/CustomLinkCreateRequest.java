package com.mrivanplays.server;

import lombok.Data;

@Data
public class CustomLinkCreateRequest {

    private String longUrl;
    private String password;
    private String keyword;
}
