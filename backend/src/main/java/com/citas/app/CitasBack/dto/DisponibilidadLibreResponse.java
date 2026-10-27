package com.citas.app.CitasBack.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/** HU-10: una franja de disponibilidad que todavía se puede reservar. */
public class DisponibilidadLibreResponse {

    private final Long id;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;

    public DisponibilidadLibreResponse(Long id, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        this.id = id;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Long getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
}
