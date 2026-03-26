package com.ciao.clinica.backend.domain.usuario.entity;

import jakarta.persistence.*;
import lombok.*;

import com.ciao.clinica.backend.domain.common.Auditable;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 150)
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "system_role", nullable = false)
    @Builder.Default
    private Boolean systemRole = false;

}