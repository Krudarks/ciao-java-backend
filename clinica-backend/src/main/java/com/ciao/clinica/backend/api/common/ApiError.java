package com.ciao.clinica.backend.api.common;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiError {

    private LocalDateTime timestamp;
    private String path;
    private ErrorCode error;
    private String message;
    private Map<String, String> fields;

}