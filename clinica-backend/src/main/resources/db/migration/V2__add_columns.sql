ALTER TABLE usuarios
ADD COLUMN fecha_actualizacion TIMESTAMP,
ADD COLUMN created_by VARCHAR(50),
ADD COLUMN updated_by VARCHAR(50);

ALTER TABLE roles
ADD COLUMN fecha_actualizacion TIMESTAMP,
ADD COLUMN created_by VARCHAR(50),
ADD COLUMN updated_by VARCHAR(50);
