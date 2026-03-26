package com.ciao.clinica.backend.audit;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

import com.ciao.clinica.backend.domain.common.Auditable;
import com.ciao.clinica.backend.security.util.SecurityUtils;


public class AuditListener {

    @PrePersist
    public void prePersist(Auditable entity) {
        entity.setFechaCreacion(LocalDateTime.now());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
    }

    @PreUpdate
    public void preUpdate(Auditable entity) {
        entity.setFechaActualizacion(LocalDateTime.now());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
    }
}