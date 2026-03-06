package com.ciao.clinica.backend.domain.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciao.clinica.backend.domain.usuario.entity.Rol;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);
}