package com.citas.app.CitasBack.dto;

import com.citas.app.CitasBack.model.Disponibilidad;
import com.citas.app.CitasBack.model.Especialidad;
import com.citas.app.CitasBack.model.Usuario;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Formas JSON que espera el frontend Vitalis (idUsuario, correo, idEspecialidad, idHorario...).
 * Nunca incluyen la contraseña ni el rol interno.
 */
public final class Vista {

    private Vista() {
    }

    public static Map<String, Object> perfil(Usuario u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idUsuario", u.getId());
        m.put("nombre", u.getNombre());
        m.put("apellido", u.getApellido());
        m.put("correo", u.getEmail());
        m.put("telefono", u.getTelefono());
        return m;
    }

    public static Map<String, Object> resumenEspecialista(Usuario u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idUsuario", u.getId());
        m.put("nombre", u.getNombre());
        m.put("apellido", u.getApellido());
        return m;
    }

    public static Map<String, Object> especialidad(Especialidad e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idEspecialidad", e.getId());
        m.put("nombreEspecialidad", e.getNombre());
        m.put("descripcion", e.getDescripcion());
        m.put("duracionMinutos", e.getDuracionMinutos());
        m.put("especialista", e.getEspecialista() == null ? null : resumenEspecialista(e.getEspecialista()));
        return m;
    }

    public static Map<String, Object> horario(Disponibilidad d, String estado) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idHorario", d.getId());
        m.put("fecha", d.getFecha().toString());
        m.put("horaInicio", d.getHoraInicio().toString());
        m.put("horaFin", d.getHoraFin().toString());
        m.put("estado", estado);
        return m;
    }
}
