package com.citas.app.CitasBack.model;

import java.util.List;

/**
 * Estados de una cita (columna estado de la tabla citas).
 *
 * - PENDIENTE: la deja HU-12 (Sprint 1) al agendar.
 * - SOLICITADA / CONFIRMADA: se aceptan por compatibilidad con el modelo del Sprint 2.
 * - CANCELADA: la deja HU-14 (o el DELETE /api/citas/cancelar/{id} del Sprint 1).
 */
public final class EstadoCita {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String SOLICITADA = "SOLICITADA";
    public static final String CONFIRMADA = "CONFIRMADA";
    public static final String CANCELADA = "CANCELADA";
    public static final String COMPLETADA = "COMPLETADA";

    /** Estados en los que una cita sigue "pendiente" y todavía se puede cancelar o reprogramar. */
    public static final List<String> PENDIENTES = List.of(PENDIENTE, SOLICITADA, CONFIRMADA);

    private EstadoCita() {
    }

    public static boolean admiteCambios(String estado) {
        return PENDIENTES.contains(estado);
    }
}
