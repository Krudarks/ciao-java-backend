package com.ciao.clinica.backend.api.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RolResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Boolean systemRole;

}