package com.citas.app.CitasBack.dto;

import com.citas.app.CitasBack.model.Cita;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * HU-13: una cita lista para mostrar. Conserva los campos del Sprint 1 (id, servicio, fechaHora,
 * estado) y agrega los del Sprint 2. No incluye datos sensibles del usuario ni del especialista.
 */
public class CitaResponse {

    private final Long id;
    private final String servicio;
    private final LocalDateTime fechaHora;
    private final String estado;
    private final LocalDate fechaCita;
    private final LocalTime horaCita;
    private final String especialidad;
    private final Long idEspecialista;
    private final String especialista;
    private final String motivoCancelacion;
    private final LocalDateTime fechaCancelacion;

    public CitaResponse(Cita cita) {
        this.id = cita.getId();
        this.servicio = cita.getServicio();
        this.fechaHora = cita.getFechaHora();
        this.estado = cita.getEstado();
        this.fechaCita = cita.getFechaHora() == null ? null : cita.getFechaHora().toLocalDate();
        this.horaCita = cita.getFechaHora() == null ? null : cita.getFechaHora().toLocalTime();
        // Las citas del Sprint 1 no tienen especialidad: se muestra el servicio que se escribió.
        this.especialidad = cita.getEspecialidad() != null ? cita.getEspecialidad().getNombre() : cita.getServicio();
        this.idEspecialista = cita.getEspecialista() != null ? cita.getEspecialista().getId() : null;
        this.especialista = cita.getEspecialista() != null
                ? cita.getEspecialista().getNombre() + " " + cita.getEspecialista().getApellido()
                : null;
        this.motivoCancelacion = cita.getMotivoCancelacion();
        this.fechaCancelacion = cita.getFechaCancelacion();
    }

    public Long getId() { return id; }
    public String getServicio() { return servicio; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getEstado() { return estado; }
    public LocalDate getFechaCita() { return fechaCita; }
    public LocalTime getHoraCita() { return horaCita; }
    public String getEspecialidad() { return especialidad; }
    public Long getIdEspecialista() { return idEspecialista; }
    public String getEspecialista() { return especialista; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public LocalDateTime getFechaCancelacion() { return fechaCancelacion; }
}
