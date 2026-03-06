package com.ciao.clinica.backend.domain.refresh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ciao.clinica.backend.domain.refresh.entity.RefreshToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revocado = true WHERE r.usuarioId = :usuarioId")
    void revokeAllByUsuarioId(Long usuarioId);
}
