-- =============================================================================
-- SCRIPT DE BASE DE DATOS POSTGRESQL - PROYECTO VITALIS (SPRINT 1)
-- =============================================================================

DROP TABLE IF EXISTS citas CASCADE;
DROP TABLE IF EXISTS disponibilidades CASCADE;
DROP TABLE IF EXISTS especialistas CASCADE;
DROP TABLE IF EXISTS especialidades CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- HU-01 / HU-02: Registro e Inicio de Sesión de Usuarios
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('PACIENTE', 'ESPECIALISTA', 'ADMIN')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- HU-06 / HU-08: Registro y Consulta de Especialidades Médicas
CREATE TABLE especialidades (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE
);

-- Relación de Especialistas con Usuarios y Especialidades
CREATE TABLE especialistas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
    especialidad_id BIGINT NOT NULL REFERENCES especialidades(id) ON DELETE CASCADE
);

-- HU-09: Definición de Horarios Disponibles
CREATE TABLE disponibilidades (
    id BIGSERIAL PRIMARY KEY,
    especialista_id BIGINT NOT NULL REFERENCES especialistas(id) ON DELETE CASCADE,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    disponible BOOLEAN DEFAULT TRUE
);

-- HU-12: Solicitud / Agendamiento de Citas
CREATE TABLE citas (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    especialista_id BIGINT NOT NULL REFERENCES especialistas(id) ON DELETE CASCADE,
    fecha_hora TIMESTAMP NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PROGRAMADA', 'COMPLETADA', 'CANCELADA')),
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar Datos Iniciales (Ejemplo / Semilla)
INSERT INTO especialidades (nombre, descripcion) VALUES 
('Medicina General', 'Atención médica primaria y preventiva'),
('Cardiología', 'Especialidad en enfermedades del corazón'),
('Pediatría', 'Atención médica integral para niños y adolescentes');
