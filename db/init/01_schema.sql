CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE localidad (
 id_localidad UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 nombre VARCHAR(100) NOT NULL,
 tipo_localidad VARCHAR(20) NOT NULL CHECK (tipo_localidad IN ('ASIENTO','AFORO_GENERAL')),
 codigo_silla VARCHAR(20),
 capacidad_total INTEGER CHECK (capacidad_total > 0),
 cupos_disponibles INTEGER CHECK (cupos_disponibles >= 0),
 precio NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
 estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE' CHECK (estado IN ('DISPONIBLE','RESERVADO','AGOTADO')),
 version INTEGER NOT NULL DEFAULT 0,
 CHECK ((tipo_localidad='ASIENTO' AND codigo_silla IS NOT NULL) OR (tipo_localidad='AFORO_GENERAL' AND capacidad_total IS NOT NULL AND cupos_disponibles IS NOT NULL AND cupos_disponibles <= capacidad_total))
);
CREATE TABLE reserva (
 id_reserva UUID PRIMARY KEY,
 id_localidad UUID NOT NULL REFERENCES localidad(id_localidad),
 usuario_id VARCHAR(100) NOT NULL,
 cantidad INTEGER NOT NULL CHECK (cantidad >= 1),
 fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT now(),
 fecha_expiracion TIMESTAMPTZ NOT NULL CHECK (fecha_expiracion > fecha_creacion),
 estado VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVA','CONFIRMADA','EXPIRADA','CANCELADA'))
);
CREATE INDEX idx_reserva_localidad ON reserva(id_localidad);
CREATE INDEX idx_reserva_expiracion_activa ON reserva(fecha_expiracion) WHERE estado='ACTIVA';
CREATE TABLE auditoria_transaccion (
 id_auditoria UUID PRIMARY KEY,
 id_reserva UUID NOT NULL REFERENCES reserva(id_reserva),
 nodo_bd_ejecutor VARCHAR(100) NOT NULL,
 tiempo_bloqueo_ms INTEGER NOT NULL CHECK (tiempo_bloqueo_ms >= 0),
 fecha_registro TIMESTAMPTZ NOT NULL DEFAULT now()
);
