package com.ciao.clinica.backend.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthTokens {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
}
