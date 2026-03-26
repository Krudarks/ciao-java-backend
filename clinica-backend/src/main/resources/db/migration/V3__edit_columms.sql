-- =========================
-- ROLES
-- =========================

-- 1. eliminar constraints si existen
ALTER TABLE roles DROP CONSTRAINT IF EXISTS fk_roles_created_by;
ALTER TABLE roles DROP CONSTRAINT IF EXISTS fk_roles_updated_by;

-- 2. cambiar tipo
ALTER TABLE roles
ALTER COLUMN created_by TYPE BIGINT USING NULL;

ALTER TABLE roles
ALTER COLUMN updated_by TYPE BIGINT USING NULL;

-- =========================
-- USUARIOS
-- =========================

ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS fk_usuarios_created_by;
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS fk_usuarios_updated_by;

ALTER TABLE usuarios
ALTER COLUMN created_by TYPE BIGINT USING NULL;

ALTER TABLE usuarios
ALTER COLUMN updated_by TYPE BIGINT USING NULL;

-- =========================
-- FKs
-- =========================

ALTER TABLE roles
ADD CONSTRAINT fk_roles_created_by
FOREIGN KEY (created_by) REFERENCES usuarios(id)
ON DELETE SET NULL;

ALTER TABLE roles
ADD CONSTRAINT fk_roles_updated_by
FOREIGN KEY (updated_by) REFERENCES usuarios(id)
ON DELETE SET NULL;

ALTER TABLE usuarios
ADD CONSTRAINT fk_usuarios_created_by
FOREIGN KEY (created_by) REFERENCES usuarios(id)
ON DELETE SET NULL;

ALTER TABLE usuarios
ADD CONSTRAINT fk_usuarios_updated_by
FOREIGN KEY (updated_by) REFERENCES usuarios(id)
ON DELETE SET NULL;

-- =========================
-- INDEXES
-- =========================

CREATE INDEX IF NOT EXISTS idx_roles_created_by ON roles(created_by);
CREATE INDEX IF NOT EXISTS idx_roles_updated_by ON roles(updated_by);

CREATE INDEX IF NOT EXISTS idx_usuarios_created_by ON usuarios(created_by);
CREATE INDEX IF NOT EXISTS idx_usuarios_updated_by ON usuarios(updated_by);