package com.ciao.clinica.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ciao.clinica.backend.domain.common.exceptions.UnauthorizedException;

import com.ciao.clinica.backend.domain.auth.dto.AuthTokens;
import com.ciao.clinica.backend.config.SecurityProperties;
import com.ciao.clinica.backend.domain.refresh.service.RefreshTokenService;
import com.ciao.clinica.backend.domain.usuario.entity.Usuario;
import com.ciao.clinica.backend.domain.usuario.service.UsuarioService;
import com.ciao.clinica.backend.security.jwt.JwtService;
import com.ciao.clinica.backend.security.service.CustomUserDetailsService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UsuarioService usuarioService;
    private final CustomUserDetailsService customUserDetailsService;
    private final SecurityProperties securityProperties;

    // ==============================
    // LOGIN
    // ==============================
    public AuthTokens login(String username, String password) {

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Usuario usuario = usuarioService.obtenerPorUsername(userDetails.getUsername());

            if (!usuario.getActivo()) {
                throw new UnauthorizedException("Usuario inactivo");
            }

            usuarioService.resetearIntentos(usuario);

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            refreshTokenService.save(
                    refreshToken,
                    usuario.getId(),
                    Duration.ofDays(securityProperties.getRefreshTokenDays()),
                    LocalDateTime.now());

            return AuthTokens.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .build();

        } catch (LockedException ex) {

            throw new UnauthorizedException("Cuenta bloqueada. Contacte al administrador.");

        } catch (BadCredentialsException ex) {

            usuarioService.registrarIntentoFallido(
                    username,
                    securityProperties.getMaxLoginAttempts());

            throw new UnauthorizedException("Credenciales incorrectas");
        }
    }

    // ==============================
    // REFRESH
    // ==============================
    public AuthTokens refresh(String refreshToken) {

        var storedToken = refreshTokenService.findByToken(refreshToken);

        if (storedToken.isRevocado()) {
            throw new UnauthorizedException("Refresh token revocado");
        }

        if (storedToken.getFechaExpiracion()
                .isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token expirado");
        }

        LocalDateTime limiteAbsoluto = storedToken.getFechaInicioSesion()
                .plusDays(securityProperties.getAbsoluteSessionDays());

        if (LocalDateTime.now().isAfter(limiteAbsoluto)) {
            throw new UnauthorizedException("Sesión expirada. Inicie sesión nuevamente.");
        }

        String username = jwtService.extractUsername(refreshToken);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        refreshTokenService.revoke(storedToken);

        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        Usuario usuario = usuarioService.obtenerPorUsername(username);

        refreshTokenService.save(
                newRefreshToken,
                usuario.getId(),
                Duration.ofDays(securityProperties.getRefreshTokenDays()),
                storedToken.getFechaInicioSesion());

        return AuthTokens.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    // ==============================
    // LOGOUT
    // ==============================
    public void logout(String username) {

        Usuario usuario = usuarioService.obtenerPorUsername(username);

        refreshTokenService.logoutUsuario(usuario.getId());
    }
}