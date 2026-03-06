package com.ciao.clinica.backend.api.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
public class UsuarioResponse {

    private Long id;
    private String username;
    private String email;
    private Boolean activo;
    private Boolean cuentaNoBloqueada;
    private LocalDateTime fechaCreacion;
    private Set<String> roles;
}