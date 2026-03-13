package com.ciao.clinica.backend.api.admin.mapper;

import org.springframework.stereotype.Component;

import com.ciao.clinica.backend.api.admin.dto.RolResponse;
import com.ciao.clinica.backend.domain.usuario.entity.Rol;

@Component
public class RolMapper {

    public RolResponse toResponse(Rol rol) {

        return RolResponse.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .activo(rol.getActivo())
                .systemRole(rol.getSystemRole())
                .build();
    }

}