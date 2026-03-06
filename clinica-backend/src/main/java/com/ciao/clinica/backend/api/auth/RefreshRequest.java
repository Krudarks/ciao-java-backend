package com.ciao.clinica.backend.api.auth;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}