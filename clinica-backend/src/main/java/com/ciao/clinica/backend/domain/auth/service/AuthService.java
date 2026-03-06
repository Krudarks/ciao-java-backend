package com.ciao.clinica.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ciao.clinica.backend.api.auth.AuthResponse;
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
    public AuthResponse login(String username, String password) {

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Usuario usuario = usuarioService.obtenerPorUsername(userDetails.getUsername());

            if (!usuario.getActivo()) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario inactivo");
            }

            usuarioService.resetearIntentos(usuario);

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            refreshTokenService.save(
                    refreshToken,
                    usuario.getId(),
                    Duration.ofDays(securityProperties.getRefreshTokenDays()),
                    LocalDateTime.now());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .build();

        } catch (LockedException ex) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Cuenta bloqueada. Contacte al administrador.");

        } catch (BadCredentialsException ex) {

            usuarioService.registrarIntentoFallido(
                    username,
                    securityProperties.getMaxLoginAttempts());

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas");
        }
    }

    // ==============================
    // REFRESH
    // ==============================
    public AuthResponse refresh(String refreshToken) {

        var storedToken = refreshTokenService.findByToken(refreshToken);

        if (storedToken.isRevocado()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token revocado");
        }

        if (storedToken.getFechaExpiracion()
                .isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token expirado");
        }

        LocalDateTime limiteAbsoluto = storedToken.getFechaInicioSesion()
                .plusDays(securityProperties.getAbsoluteSessionDays());

        if (LocalDateTime.now().isAfter(limiteAbsoluto)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Sesión expirada. Inicie sesión nuevamente.");
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

        return AuthResponse.builder()
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