package com.ciao.clinica.backend.api.admin.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UsuarioRolesRequest {

    private Set<String> roles;

}