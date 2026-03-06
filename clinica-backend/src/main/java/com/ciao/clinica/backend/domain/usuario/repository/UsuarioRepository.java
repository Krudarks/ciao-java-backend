package com.ciao.clinica.backend.domain.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciao.clinica.backend.domain.usuario.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<Usuario> findByCuentaNoBloqueadaFalse();
}
