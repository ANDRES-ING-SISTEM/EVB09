package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.model.BloqueoHorario;
import com.citas.app.CitasBack.model.EstadoBloqueo;
import com.citas.app.CitasBack.model.EstadoCita;
import com.citas.app.CitasBack.repository.BloqueoHorarioRepository;
import com.citas.app.CitasBack.repository.CitaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Reglas de agenda compartidas por HU-10 (consultar disponibilidad), HU-11 (bloquear horario),
 * HU-12 (solicitar cita) y HU-15 (reprogramar cita), para que todas respondan lo mismo a
 * "¿este horario está libre?".
 */
@Component
public class ReglasAgenda {

    private final CitaRepository citaRepository;
    private final BloqueoHorarioRepository bloqueoHorarioRepository;

    public ReglasAgenda(CitaRepository citaRepository, BloqueoHorarioRepository bloqueoHorarioRepository) {
        this.citaRepository = citaRepository;
        this.bloqueoHorarioRepository = bloqueoHorarioRepository;
    }

    /** ¿El especialista tiene una cita activa (no CANCELADA) exactamente a esa fecha y hora? */
    public boolean estaOcupado(Long idEspecialista, LocalDateTime inicio) {
        return citaRepository.existsByEspecialistaIdAndFechaHoraAndEstadoNot(
                idEspecialista, inicio, EstadoCita.CANCELADA);
    }

    /** ¿Alguna cita activa del especialista empieza dentro de [desde, hasta)? */
    public boolean hayCitaEnRango(Long idEspecialista, LocalDateTime desde, LocalDateTime hasta) {
        return !citaRepository
                .findByEspecialistaIdAndFechaHoraGreaterThanEqualAndFechaHoraLessThanAndEstadoNot(
                        idEspecialista, desde, hasta, EstadoCita.CANCELADA)
                .isEmpty();
    }

    /** ¿Una hora puntual cae dentro de un bloqueo activo del especialista? */
    public boolean estaBloqueado(Long idEspecialista, LocalDateTime inicio) {
        LocalTime hora = inicio.toLocalTime();
        return bloqueosActivos(idEspecialista, inicio.toLocalDate()).stream()
                .anyMatch(b -> !hora.isBefore(b.getHoraInicio()) && hora.isBefore(b.getHoraFin()));
    }

    /** ¿El rango [inicio, fin) se cruza con algún bloqueo activo del especialista ese día? */
    public boolean seCruzaConBloqueo(Long idEspecialista, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        return bloqueosActivos(idEspecialista, fecha).stream()
                .anyMatch(b -> inicio.isBefore(b.getHoraFin()) && b.getHoraInicio().isBefore(fin));
    }

    private List<BloqueoHorario> bloqueosActivos(Long idEspecialista, LocalDate fecha) {
        return bloqueoHorarioRepository.findByEspecialistaIdAndFechaAndEstado(
                idEspecialista, fecha, EstadoBloqueo.ACTIVO);
    }
}
