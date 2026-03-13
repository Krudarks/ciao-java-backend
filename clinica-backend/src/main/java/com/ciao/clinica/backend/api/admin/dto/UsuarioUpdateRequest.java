package com.ciao.clinica.backend.api.admin.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UsuarioUpdateRequest {

    @Email(message = "Formato de email inválido")
    private String email;
    
    private Boolean activo;

}