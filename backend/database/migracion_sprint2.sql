-- =============================================================================
-- MIGRACIÓN SPRINT 1 -> SPRINT 2 (PostgreSQL / Supabase)
-- =============================================================================
-- Qué hace: agrega columnas y tablas nuevas SIN borrar ni modificar datos existentes.
-- Se puede ejecutar varias veces (usa IF NOT EXISTS).
--
-- IMPORTANTE: con spring.jpa.hibernate.ddl-auto=update, Hibernate ya crea todo esto solo al
-- arrancar el backend. Este script sirve si prefieres crearlo a mano, o si el usuario de la
-- base de datos de Render/Supabase no tiene permisos para alterar tablas.
--
-- Nota: este script parte del esquema que genera el backend (entidades JPA: tablas usuarios,
-- citas con usuario_id / servicio / fecha_hora, etc.). Es el esquema con el que funciona el
-- Sprint 1 en local. NO parte de schema_postgresql.sql, que quedó desactualizado frente a las
-- entidades (por ejemplo, su CHECK de estado no acepta 'PENDIENTE', que es lo que guarda HU-12).
-- =============================================================================

-- HU-03 / HU-04: datos del perfil y del especialista
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS telefono VARCHAR(255);
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS registro_medico VARCHAR(255);

-- HU-07: duración de la consulta
ALTER TABLE especialidades ADD COLUMN IF NOT EXISTS duracion_minutos INTEGER;

-- HU-12 / HU-13 / HU-14 / HU-15: la cita puede llevar especialista y especialidad, y datos de cancelación
ALTER TABLE citas ADD COLUMN IF NOT EXISTS especialista_id BIGINT REFERENCES usuarios(id);
ALTER TABLE citas ADD COLUMN IF NOT EXISTS especialidad_id BIGINT REFERENCES especialidades(id);
ALTER TABLE citas ADD COLUMN IF NOT EXISTS motivo_cancelacion VARCHAR(200);
ALTER TABLE citas ADD COLUMN IF NOT EXISTS fecha_cancelacion TIMESTAMP;
ALTER TABLE citas ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP;

-- HU-11: bloqueos de horario
CREATE TABLE IF NOT EXISTS bloqueos_horario (
    id_bloqueo BIGSERIAL PRIMARY KEY,
    especialista_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    motivo VARCHAR(200),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_liberacion TIMESTAMP
);

-- HU-15: historial de reprogramaciones
CREATE TABLE IF NOT EXISTS reprogramaciones_cita (
    id_reprogramacion BIGSERIAL PRIMARY KEY,
    cita_id BIGINT NOT NULL REFERENCES citas(id) ON DELETE CASCADE,
    fecha_anterior DATE NOT NULL,
    hora_anterior TIME NOT NULL,
    fecha_nueva DATE NOT NULL,
    hora_nueva TIME NOT NULL,
    motivo VARCHAR(200),
    fecha_registro TIMESTAMP NOT NULL
);
