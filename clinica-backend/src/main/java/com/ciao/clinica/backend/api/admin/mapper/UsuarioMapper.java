package com.ciao.clinica.backend.api.admin.mapper;

import com.ciao.clinica.backend.api.admin.dto.UsuarioResponse;
import com.ciao.clinica.backend.domain.usuario.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .activo(usuario.getActivo())
                .cuentaNoBloqueada(usuario.getCuentaNoBloqueada())
                .fechaCreacion(usuario.getFechaCreacion())
                .roles(
                        usuario.getRoles()
                                .stream()
                                .map(r -> r.getNombre())
                                .collect(Collectors.toSet()))
                .build();
    }
}