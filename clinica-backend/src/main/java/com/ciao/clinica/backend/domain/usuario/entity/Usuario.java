package com.ciao.clinica.backend.domain.usuario.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

import com.ciao.clinica.backend.domain.common.Auditable;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "cuenta_no_expirada", nullable = false)
    @Builder.Default
    private Boolean cuentaNoExpirada = true;

    @Column(name = "cuenta_no_bloqueada", nullable = false)
    @Builder.Default
    private Boolean cuentaNoBloqueada = true;

    @Column(name = "credenciales_no_expiradas", nullable = false)
    @Builder.Default
    private Boolean credencialesNoExpiradas = true;

    @Column(name = "intentos_fallidos", nullable = false)
    @Builder.Default
    private Integer intentosFallidos = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles;
}