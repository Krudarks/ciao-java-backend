package com.ciao.clinica.backend.api.admin.dto;

import lombok.Data;
import java.util.Set;

import jakarta.validation.constraints.NotEmpty;

@Data
public class UsuarioRolesRequest {

    @NotEmpty(message = "Debe especificar al menos un rol")
    private Set<String> roles;

}