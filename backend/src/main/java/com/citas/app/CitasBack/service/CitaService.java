package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.dto.CitaVista;
import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Cita;
import com.citas.app.CitasBack.model.Disponibilidad;
import com.citas.app.CitasBack.model.Especialidad;
import com.citas.app.CitasBack.model.EstadoCita;
import com.citas.app.CitasBack.model.ReprogramacionCita;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.CitaRepository;
import com.citas.app.CitasBack.repository.DisponibilidadRepository;
import com.citas.app.CitasBack.repository.EspecialidadRepository;
import com.citas.app.CitasBack.repository.ReprogramacionCitaRepository;
import com.citas.app.CitasBack.util.Reloj;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HU-12 a HU-15: solicitar, consultar, cancelar y reprogramar citas. El paciente SIEMPRE es el de
 * la sesión: ninguna operación recibe un idUsuario del cliente.
 */
@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final EspecialidadRepository especialidadRepository;
    private final ReprogramacionCitaRepository reprogramacionCitaRepository;

    @Value("${citas.cancelacion.horas-minimas:24}")
    private long horasMinimasCancelar;

    @Value("${citas.reprogramacion.horas-minimas:24}")
    private long horasMinimasReprogramar;

    public CitaService(CitaRepository citaRepository,
                       DisponibilidadRepository disponibilidadRepository,
                       EspecialidadRepository especialidadRepository,
                       ReprogramacionCitaRepository reprogramacionCitaRepository) {
        this.citaRepository = citaRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.especialidadRepository = especialidadRepository;
        this.reprogramacionCitaRepository = reprogramacionCitaRepository;
    }

    @Transactional
    public Map<String, Object> solicitar(Usuario paciente, Long idHorario, String motivo) {
        Disponibilidad h = disponibilidadRepository.findByIdParaActualizar(idHorario)
                .orElseThrow(() -> ApiException.noEncontrado("No existe el horario indicado."));
        if (citaRepository.existsByUsuarioIdAndIdHorarioAndEstadoNot(paciente.getId(), idHorario, EstadoCita.CANCELADA)) {
            throw ApiException.conflicto("Ya tienes una cita en este horario.");
        }
        exigirReservable(h);

        Especialidad esp = especialidadRepository
                .findFirstByEspecialistaIdAndActivoTrueOrderByIdAsc(h.getEspecialista().getId()).orElse(null);

        Cita c = new Cita();
        c.setServicio(esp != null ? esp.getNombre() : "Consulta");
        c.setFechaHora(h.getFecha().atTime(h.getHoraInicio()));
        c.setEstado(EstadoCita.PENDIENTE);
        c.setUsuario(paciente);
        c.setEspecialista(h.getEspecialista());
        c.setEspecialidad(esp);
        c.setIdHorario(h.getId());
        c.setMotivo(motivo == null || motivo.isBlank() ? null : motivo.trim());
        c = citaRepository.save(c);
        return CitaVista.creada(c);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarDe(Long idUsuario) {
        List<Map<String, Object>> salida = new ArrayList<>();
        for (Cita c : citaRepository.findByUsuarioIdOrderByFechaHoraDesc(idUsuario)) {
            salida.add(CitaVista.completa(c));
        }
        return salida;
    }

    @Transactional
    public void cancelar(Long idCita, Usuario paciente) {
        Cita c = propia(idCita, paciente);
        if (!EstadoCita.admiteCambios(c.getEstado())) {
            throw ApiException.conflicto("Esta cita no puede cancelarse porque ya fue cancelada o ya ocurrió.");
        }
        if (Reloj.ahora().plusHours(horasMinimasCancelar).isAfter(c.getFechaHora())) {
            throw ApiException.conflicto("Solo puedes cancelar con al menos " + horasMinimasCancelar
                    + " horas de anticipación.");
        }
        c.setEstado(EstadoCita.CANCELADA);
        c.setFechaCancelacion(Reloj.ahora());
        citaRepository.save(c);
    }

    @Transactional
    public Map<String, Object> reprogramar(Long idCita, Long idHorarioNuevo, Usuario paciente) {
        Cita c = propia(idCita, paciente);
        if (!EstadoCita.admiteCambios(c.getEstado())) {
            throw ApiException.conflicto("Esta cita no puede reprogramarse porque ya fue cancelada o ya ocurrió.");
        }
        if (Reloj.ahora().plusHours(horasMinimasReprogramar).isAfter(c.getFechaHora())) {
            throw ApiException.conflicto("Solo puedes reprogramar con al menos " + horasMinimasReprogramar
                    + " horas de anticipación.");
        }
        Disponibilidad nuevo = disponibilidadRepository.findByIdParaActualizar(idHorarioNuevo)
                .orElseThrow(() -> ApiException.noEncontrado("No existe el horario indicado."));
        if (idHorarioNuevo.equals(c.getIdHorario())) {
            throw ApiException.campo("idHorario", "El nuevo horario es el mismo de tu cita actual.");
        }
        if (c.getEspecialista() != null && !nuevo.getEspecialista().getId().equals(c.getEspecialista().getId())) {
            throw ApiException.campo("idHorario", "El nuevo horario debe ser del mismo especialista.");
        }
        exigirReservable(nuevo);

        LocalDateTime anterior = c.getFechaHora();
        LocalDateTime siguiente = nuevo.getFecha().atTime(nuevo.getHoraInicio());

        ReprogramacionCita historial = new ReprogramacionCita();
        historial.setCita(c);
        historial.setFechaAnterior(anterior.toLocalDate());
        historial.setHoraAnterior(anterior.toLocalTime());
        historial.setFechaNueva(siguiente.toLocalDate());
        historial.setHoraNueva(siguiente.toLocalTime());
        historial.setFechaRegistro(Reloj.ahora());
        reprogramacionCitaRepository.save(historial);

        c.setFechaHora(siguiente);
        c.setIdHorario(nuevo.getId());
        c = citaRepository.save(c);
        return CitaVista.creada(c);
    }

    /** Cita del paciente de la sesión. Si no existe o es de otra persona, responde 404 igual. */
    private Cita propia(Long idCita, Usuario paciente) {
        Cita c = citaRepository.findById(idCita)
                .orElseThrow(() -> ApiException.noEncontrado("No existe una cita con id " + idCita));
        if (c.getUsuario() == null || !c.getUsuario().getId().equals(paciente.getId())) {
            throw ApiException.noEncontrado("No existe una cita con id " + idCita);
        }
        return c;
    }

    private void exigirReservable(Disponibilidad h) {
        boolean bloqueado = Boolean.FALSE.equals(h.getDisponible());
        boolean pasado = !h.getFecha().atTime(h.getHoraInicio()).isAfter(Reloj.ahora());
        boolean ocupado = citaRepository.existsByIdHorarioAndEstadoNot(h.getId(), EstadoCita.CANCELADA);
        if (bloqueado || pasado || ocupado) {
            throw ApiException.conflicto("El horario ya no está disponible.");
        }
    }
}
