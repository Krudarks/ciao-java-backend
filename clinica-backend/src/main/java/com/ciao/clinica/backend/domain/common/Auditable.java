package com.ciao.clinica.backend.domain.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.ciao.clinica.backend.audit.AuditListener;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditListener.class)
public abstract class Auditable {

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

}