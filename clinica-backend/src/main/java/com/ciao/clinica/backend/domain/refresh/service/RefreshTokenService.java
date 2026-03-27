package com.ciao.clinica.backend.domain.refresh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.ciao.clinica.backend.domain.common.exceptions.UnauthorizedException;
import com.ciao.clinica.backend.domain.refresh.entity.RefreshToken;
import com.ciao.clinica.backend.domain.refresh.repository.RefreshTokenRepository;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken save(String token, Long usuarioId, Duration expiration, LocalDateTime fechaInicioSesion) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .usuarioId(usuarioId)
                .fechaExpiracion(LocalDateTime.now().plus(expiration))
                .revocado(false)
                .fechaInicioSesion(fechaInicioSesion)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Refresh token no encontrado"));
    }

    public void revoke(RefreshToken refreshToken) {
        refreshToken.setRevocado(true);
        refreshTokenRepository.save(refreshToken);
    }

    public void logoutUsuario(Long usuarioId) {
        refreshTokenRepository.revokeAllByUsuarioId(usuarioId);
    }

}