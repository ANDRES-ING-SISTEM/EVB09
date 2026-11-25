package com.citas.app.CitasBack.dto;

import com.citas.app.CitasBack.model.Cita;

import java.util.LinkedHashMap;
import java.util.Map;

/** Formas JSON de una cita para el frontend. */
public final class CitaVista {

    private CitaVista() {
    }

    /** Respuesta de POST /api/citas y PUT /api/citas/{id}/reprogramar (tipo CitaCreada del frontend). */
    public static Map<String, Object> creada(Cita c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idCita", c.getId());
        m.put("especialidad", nombreEspecialidad(c));
        m.put("fecha", c.getFechaHora().toLocalDate().toString());
        m.put("horaInicio", c.getFechaHora().toLocalTime().toString());
        m.put("estado", c.getEstado());
        return m;
    }

    /** Elemento de GET /api/citas/usuario/{id} (tipo Cita del frontend). */
    public static Map<String, Object> completa(Cita c) {
        Map<String, Object> esp = new LinkedHashMap<>();
        esp.put("idEspecialidad", c.getEspecialidad() == null ? null : c.getEspecialidad().getId());
        esp.put("nombreEspecialidad", nombreEspecialidad(c));
        esp.put("duracionMinutos", c.getEspecialidad() == null ? null : c.getEspecialidad().getDuracionMinutos());

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idCita", c.getId());
        m.put("especialidad", esp);
        m.put("especialista", c.getEspecialista() == null ? null : Vista.resumenEspecialista(c.getEspecialista()));
        m.put("fechaCita", c.getFechaHora().toLocalDate().toString());
        m.put("horaCita", c.getFechaHora().toLocalTime().toString());
        m.put("estado", c.getEstado());
        m.put("motivo", c.getMotivo());
        return m;
    }

    private static String nombreEspecialidad(Cita c) {
        if (c.getEspecialidad() != null) {
            return c.getEspecialidad().getNombre();
        }
        return c.getServicio() == null ? "Consulta" : c.getServicio();
    }
}
