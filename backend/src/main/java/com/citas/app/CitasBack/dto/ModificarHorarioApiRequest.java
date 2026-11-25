package com.citas.app.CitasBack.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/** HU-22. Acepta "fecha" (frontend) o "fechaEspecifica" (nombre usado por el equipo); lo no enviado se conserva. */
public class ModificarHorarioApiRequest {

    private LocalDate fecha;
    private LocalDate fechaEspecifica;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalDate getFechaEspecifica() { return fechaEspecifica; }
    public void setFechaEspecifica(LocalDate fechaEspecifica) { this.fechaEspecifica = fechaEspecifica; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}
