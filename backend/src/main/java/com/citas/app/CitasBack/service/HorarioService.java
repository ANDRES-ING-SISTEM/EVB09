package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.dto.HorarioRequest;
import com.citas.app.CitasBack.dto.Vista;
import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Disponibilidad;
import com.citas.app.CitasBack.model.Especialidad;
import com.citas.app.CitasBack.model.EstadoCita;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.CitaRepository;
import com.citas.app.CitasBack.repository.DisponibilidadRepository;
import com.citas.app.CitasBack.repository.EspecialidadRepository;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import com.citas.app.CitasBack.util.Reloj;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HU-09, HU-10 y HU-11: horarios del especialista. Un horario es una fila de Disponibilidad; su
 * estado se calcula: RESERVADO si tiene una cita activa, BLOQUEADO si el especialista lo bloqueó
 * (disponible = false) y LIBRE en otro caso.
 */
@Service
public class HorarioService {

    public static final String LIBRE = "LIBRE";
    public static final String RESERVADO = "RESERVADO";
    public static final String BLOQUEADO = "BLOQUEADO";

    private final DisponibilidadRepository disponibilidadRepository;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspecialidadRepository especialidadRepository;

    public HorarioService(DisponibilidadRepository disponibilidadRepository,
                          CitaRepository citaRepository,
                          UsuarioRepository usuarioRepository,
                          EspecialidadRepository especialidadRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.especialidadRepository = especialidadRepository;
    }

    @Transactional
    public Map<String, Object> crear(Usuario especialista, HorarioRequest r) {
        if (!r.getHoraInicio().isBefore(r.getHoraFin())) {
            throw ApiException.campo("horaFin", "La hora de fin debe ser posterior a la hora de inicio.");
        }
        if (!r.getFecha().atTime(r.getHoraInicio()).isAfter(Reloj.ahora())) {
            throw ApiException.campo("fecha", "El horario debe ser posterior al momento actual.");
        }
        boolean seSuperpone = disponibilidadRepository
                .findByEspecialistaIdAndFecha(especialista.getId(), r.getFecha()).stream()
                .anyMatch(d -> r.getHoraInicio().isBefore(d.getHoraFin()) && d.getHoraInicio().isBefore(r.getHoraFin()));
        if (seSuperpone) {
            throw ApiException.conflicto("Ya tienes un horario que se superpone con ese rango.");
        }
        Disponibilidad d = new Disponibilidad();
        d.setEspecialista(especialista);
        d.setFecha(r.getFecha());
        d.setHoraInicio(r.getHoraInicio());
        d.setHoraFin(r.getHoraFin());
        d.setDisponible(true);
        d = disponibilidadRepository.save(d);
        return Vista.horario(d, LIBRE);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarDeEspecialista(Long idEspecialista) {
        usuarioRepository.findById(idEspecialista)
                .filter(u -> u.getRol() == Usuario.Rol.ESPECIALISTA)
                .orElseThrow(() -> ApiException.noEncontrado("No existe un especialista con id " + idEspecialista));
        List<Disponibilidad> lista =
                disponibilidadRepository.findByEspecialistaIdOrderByFechaAscHoraInicioAsc(idEspecialista);
        Set<Long> ocupados = ocupados(lista);
        List<Map<String, Object>> salida = new ArrayList<>();
        for (Disponibilidad d : lista) {
            salida.add(Vista.horario(d, estado(d, ocupados)));
        }
        return salida;
    }

    /** Solo horarios LIBRES y futuros del especialista de esa especialidad, opcionalmente de un día. */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> disponibles(Long idEspecialidad, LocalDate fecha) {
        Especialidad e = especialidadRepository.findById(idEspecialidad)
                .orElseThrow(() -> ApiException.noEncontrado("No existe una especialidad con id " + idEspecialidad));
        if (e.getEspecialista() == null) {
            return List.of();
        }
        List<Disponibilidad> lista = disponibilidadRepository
                .findByEspecialistaIdOrderByFechaAscHoraInicioAsc(e.getEspecialista().getId());
        Set<Long> ocupados = ocupados(lista);
        List<Map<String, Object>> salida = new ArrayList<>();
        for (Disponibilidad d : lista) {
            boolean futuro = d.getFecha().atTime(d.getHoraInicio()).isAfter(Reloj.ahora());
            boolean esElDia = fecha == null || fecha.equals(d.getFecha());
            if (futuro && esElDia && LIBRE.equals(estado(d, ocupados))) {
                salida.add(Vista.horario(d, LIBRE));
            }
        }
        return salida;
    }

    @Transactional
    public Map<String, Object> bloquear(Long idHorario, Usuario especialista) {
        Disponibilidad d = propio(idHorario, especialista);
        if (citaRepository.existsByIdHorarioAndEstadoNot(idHorario, EstadoCita.CANCELADA)) {
            throw ApiException.conflicto(
                    "Este horario tiene una cita reservada. Cancélala o reprográmala antes de bloquearlo.");
        }
        d.setDisponible(false);
        d = disponibilidadRepository.save(d);
        return Vista.horario(d, BLOQUEADO);
    }

    @Transactional
    public Map<String, Object> desbloquear(Long idHorario, Usuario especialista) {
        Disponibilidad d = propio(idHorario, especialista);
        d.setDisponible(true);
        d = disponibilidadRepository.save(d);
        boolean reservado = citaRepository.existsByIdHorarioAndEstadoNot(idHorario, EstadoCita.CANCELADA);
        return Vista.horario(d, reservado ? RESERVADO : LIBRE);
    }

    private Disponibilidad propio(Long idHorario, Usuario especialista) {
        Disponibilidad d = disponibilidadRepository.findByIdParaActualizar(idHorario)
                .orElseThrow(() -> ApiException.noEncontrado("No se encontró el horario con ID: " + idHorario));
        if (!d.getEspecialista().getId().equals(especialista.getId())) {
            throw ApiException.prohibido();
        }
        return d;
    }

    private Set<Long> ocupados(List<Disponibilidad> lista) {
        if (lista.isEmpty()) {
            return Set.of();
        }
        List<Long> ids = new ArrayList<>();
        for (Disponibilidad d : lista) {
            ids.add(d.getId());
        }
        return new HashSet<>(citaRepository.idsHorariosOcupados(ids, EstadoCita.CANCELADA));
    }

    private String estado(Disponibilidad d, Set<Long> ocupados) {
        if (ocupados.contains(d.getId())) {
            return RESERVADO;
        }
        return Boolean.FALSE.equals(d.getDisponible()) ? BLOQUEADO : LIBRE;
    }
}
