package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.model.Cita;
import com.citas.app.CitasBack.model.EstadoCita;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.CitaRepository;
import com.citas.app.CitasBack.repository.EspecialidadRepository;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import com.citas.app.CitasBack.util.Reloj;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    // Sprint 2: usados solo cuando la cita trae especialista / especialidad.
    private final EspecialidadRepository especialidadRepository;
    private final ReglasAgenda reglasAgenda;

    public CitaService(CitaRepository citaRepository,
                       UsuarioRepository usuarioRepository,
                       EspecialidadRepository especialidadRepository,
                       ReglasAgenda reglasAgenda) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.especialidadRepository = especialidadRepository;
        this.reglasAgenda = reglasAgenda;
    }

    // HU-12 (Sprint 1)
    public Cita agendarCita(Cita cita, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        cita.setUsuario(usuario);
        cita.setEstado(EstadoCita.PENDIENTE);
        validarAgendaDelEspecialista(cita);
        return citaRepository.save(cita);
    }

    /**
     * Sprint 2: si la cita se pide con un especialista, se respetan HU-10 y HU-11 (no se puede
     * reservar un horario ocupado ni bloqueado). Si viene sin especialista (como en el Sprint 1),
     * no se valida nada y todo funciona exactamente igual que antes.
     */
    private void validarAgendaDelEspecialista(Cita cita) {
        if (cita.getEspecialista() == null) {
            return;
        }
        Long idEspecialista = cita.getEspecialista().getId();
        if (idEspecialista == null) {
            throw new IllegalArgumentException("Debe indicar el id del especialista");
        }
        Usuario especialista = usuarioRepository.findById(idEspecialista)
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe un especialista con id " + idEspecialista));
        if (especialista.getRol() != Usuario.Rol.ESPECIALISTA) {
            throw new IllegalArgumentException("El usuario indicado no es un especialista");
        }
        cita.setEspecialista(especialista);

        if (cita.getEspecialidad() != null) {
            Long idEspecialidad = cita.getEspecialidad().getId();
            if (idEspecialidad == null) {
                throw new IllegalArgumentException("Debe indicar el id de la especialidad");
            }
            cita.setEspecialidad(especialidadRepository.findById(idEspecialidad)
                    .orElseThrow(() -> new NoSuchElementException(
                            "No existe una especialidad con id " + idEspecialidad)));
        }

        LocalDateTime inicio = cita.getFechaHora();
        if (inicio == null) {
            throw new IllegalArgumentException("La fecha y hora de la cita son obligatorias");
        }
        if (!inicio.isAfter(Reloj.ahora())) {
            throw new IllegalArgumentException("La fecha y hora de la cita deben ser posteriores al momento actual");
        }
        if (reglasAgenda.estaOcupado(idEspecialista, inicio)) {
            throw new IllegalArgumentException("Ese horario ya está ocupado por otra cita");
        }
        if (reglasAgenda.estaBloqueado(idEspecialista, inicio)) {
            throw new IllegalArgumentException("Ese horario está bloqueado por el especialista");
        }
    }

    // HU-13 (Sprint 2): el controlador la convierte a CitaResponse. Más recientes primero.
    public List<Cita> obtenerCitasPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new NoSuchElementException("No existe un usuario con id " + usuarioId);
        }
        return citaRepository.findByUsuarioId(usuarioId).stream()
                .sorted(Comparator.comparing(Cita::getFechaHora).reversed())
                .toList();
    }

    public Cita actualizarCita(Long citaId, Cita nuevaCitaData) {
        Cita citaExistente = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        citaExistente.setServicio(nuevaCitaData.getServicio());
        citaExistente.setFechaHora(nuevaCitaData.getFechaHora());
        return citaRepository.save(citaExistente);
    }

    public void cancelarCita(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setEstado(EstadoCita.CANCELADA);
        citaRepository.save(cita);
    }
}
