-- =========================
-- ROLES
-- =========================
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    system_role BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- USUARIOS
-- =========================
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    cuenta_no_expirada BOOLEAN NOT NULL DEFAULT TRUE,
    cuenta_no_bloqueada BOOLEAN NOT NULL DEFAULT TRUE,
    credenciales_no_expiradas BOOLEAN NOT NULL DEFAULT TRUE,
    intentos_fallidos INT NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- RELACIÓN USUARIO - ROL
-- =========================
CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_rol
        FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- =========================
-- REFRESH TOKENS
-- =========================
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    fecha_expiracion TIMESTAMP NOT NULL,
    fecha_inicio_sesion TIMESTAMP NOT NULL,
    revocado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_refresh_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- =========================
-- ÍNDICES
-- =========================
CREATE INDEX idx_usuario_username ON usuarios(username);
CREATE INDEX idx_usuario_email ON usuarios(email);
CREATE INDEX idx_refresh_token ON refresh_tokens(token);

-- =========================
-- ROLES DEL SISTEMA
-- =========================
INSERT INTO roles (nombre, descripcion, system_role)
VALUES
('ADMIN', 'Administrador del sistema', true),
('DOCTOR', 'Doctor del sistema', true),
('ALUMNO', 'Alumno del sistema', true);

-- =========================
-- USUARIO ADMIN INICIAL
-- =========================
INSERT INTO usuarios (
    username,
    email,
    password,
    activo,
    cuenta_no_expirada,
    cuenta_no_bloqueada,
    credenciales_no_expiradas,
    intentos_fallidos
)
VALUES (
    'admin',
    'admin@clinica.com',
    '$2a$10$HOXSr97FC1R/E9hwXaCrU.CQQLaIoePngJ7pgyEvzKCyyKEtuInnq',
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    0
);

-- =========================
-- VINCULAR ADMIN CON ROL ADMIN
-- =========================
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u, roles r
WHERE u.username = 'admin'
  AND r.nombre = 'ADMIN';