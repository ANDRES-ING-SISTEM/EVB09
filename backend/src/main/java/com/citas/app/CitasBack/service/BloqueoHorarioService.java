package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.dto.BloqueoRequest;
import com.citas.app.CitasBack.model.BloqueoHorario;
import com.citas.app.CitasBack.model.EstadoBloqueo;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.BloqueoHorarioRepository;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import com.citas.app.CitasBack.util.Reloj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;

/** HU-11: bloquear y desbloquear horarios de un especialista. */
@Service
public class BloqueoHorarioService {

    @Autowired
    private BloqueoHorarioRepository bloqueoHorarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReglasAgenda reglasAgenda;

    @Transactional
    public BloqueoHorario bloquear(Long idEspecialista, BloqueoRequest request) {
        Usuario especialista = buscarEspecialista(idEspecialista);

        if (!request.getHoraInicio().isBefore(request.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }
        if (request.getFecha().isBefore(Reloj.hoy())) {
            throw new IllegalArgumentException("No se puede bloquear un horario en una fecha pasada");
        }
        if (request.getFecha().equals(Reloj.hoy())
                && !request.getHoraInicio().isAfter(Reloj.ahora().toLocalTime())) {
            throw new IllegalArgumentException("No se puede bloquear un horario que ya pasó");
        }

        // No se puede bloquear un rango que ya tiene una cita activa (no CANCELADA).
        if (reglasAgenda.hayCitaEnRango(idEspecialista,
                request.getFecha().atTime(request.getHoraInicio()),
                request.getFecha().atTime(request.getHoraFin()))) {
            throw new IllegalArgumentException(
                    "El horario seleccionado tiene citas programadas. Cancélalas o reprográmalas antes de bloquearlo");
        }

        // No puede cruzarse con otro bloqueo activo.
        if (reglasAgenda.seCruzaConBloqueo(idEspecialista, request.getFecha(),
                request.getHoraInicio(), request.getHoraFin())) {
            throw new IllegalArgumentException("Ya existe un bloqueo activo que se cruza con ese horario");
        }

        BloqueoHorario bloqueo = new BloqueoHorario();
        bloqueo.setEspecialista(especialista);
        bloqueo.setFecha(request.getFecha());
        bloqueo.setHoraInicio(request.getHoraInicio());
        bloqueo.setHoraFin(request.getHoraFin());
        bloqueo.setMotivo(request.getMotivo() == null || request.getMotivo().isBlank()
                ? null : request.getMotivo().trim());
        bloqueo.setEstado(EstadoBloqueo.ACTIVO);
        bloqueo.setFechaCreacion(Reloj.ahora());
        return bloqueoHorarioRepository.save(bloqueo);
    }

    @Transactional
    public BloqueoHorario desbloquear(Long idBloqueo) {
        BloqueoHorario bloqueo = bloqueoHorarioRepository.findById(idBloqueo)
                .orElseThrow(() -> new NoSuchElementException("No existe un bloqueo con id " + idBloqueo));
        if (!EstadoBloqueo.ACTIVO.equals(bloqueo.getEstado())) {
            throw new IllegalArgumentException("El bloqueo ya había sido liberado");
        }
        bloqueo.setEstado(EstadoBloqueo.LIBERADO);
        bloqueo.setFechaLiberacion(Reloj.ahora());
        return bloqueoHorarioRepository.save(bloqueo);
    }

    /** Bloqueos activos del especialista; si se envía fecha, solo los de ese día. */
    public List<BloqueoHorario> listarActivos(Long idEspecialista, String fechaTexto) {
        buscarEspecialista(idEspecialista);
        if (fechaTexto == null || fechaTexto.isBlank()) {
            return bloqueoHorarioRepository.findByEspecialistaIdAndEstadoOrderByFechaAscHoraInicioAsc(
                    idEspecialista, EstadoBloqueo.ACTIVO);
        }
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaTexto.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha no tiene un formato válido (use AAAA-MM-DD)");
        }
        return bloqueoHorarioRepository.findByEspecialistaIdAndFechaAndEstado(
                idEspecialista, fecha, EstadoBloqueo.ACTIVO);
    }

    private Usuario buscarEspecialista(Long idEspecialista) {
        Usuario usuario = usuarioRepository.findById(idEspecialista)
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe un especialista con id " + idEspecialista));
        if (usuario.getRol() != Usuario.Rol.ESPECIALISTA) {
            throw new IllegalArgumentException("El usuario indicado no es un especialista");
        }
        return usuario;
    }
}
