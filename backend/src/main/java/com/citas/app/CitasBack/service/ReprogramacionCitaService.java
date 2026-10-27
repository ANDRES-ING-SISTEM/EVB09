package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.dto.ReprogramarCitaRequest;
import com.citas.app.CitasBack.model.Cita;
import com.citas.app.CitasBack.model.EstadoCita;
import com.citas.app.CitasBack.model.ReprogramacionCita;
import com.citas.app.CitasBack.repository.CitaRepository;
import com.citas.app.CitasBack.repository.ReprogramacionCitaRepository;
import com.citas.app.CitasBack.util.Reloj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/** HU-15: mover una cita pendiente a otra fecha y hora. */
@Service
public class ReprogramacionCitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private ReprogramacionCitaRepository reprogramacionCitaRepository;

    @Autowired
    private ReglasAgenda reglasAgenda;

    // Anticipación mínima (en horas). Se cambia en application.properties:
    // citas.reprogramacion.horas-minimas=24
    @Value("${citas.reprogramacion.horas-minimas:24}")
    private long horasMinimas;

    /**
     * La cita conserva su id y su estado; el horario anterior queda libre porque la cita ya no lo
     * ocupa, y el cambio se registra en reprogramaciones_cita (misma transacción).
     */
    @Transactional
    public ReprogramacionCita reprogramar(Long idCita, ReprogramarCitaRequest request) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new NoSuchElementException("No existe una cita con id " + idCita));

        if (!request.getIdUsuario().equals(cita.getUsuario().getId())) {
            throw new IllegalArgumentException("La cita no pertenece al usuario indicado");
        }
        if (!EstadoCita.admiteCambios(cita.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden reprogramar citas pendientes");
        }

        LocalDateTime inicioActual = cita.getFechaHora();
        if (Reloj.ahora().plusHours(horasMinimas).isAfter(inicioActual)) {
            throw new IllegalArgumentException(
                    "Solo se puede reprogramar con al menos " + horasMinimas + " horas de anticipación");
        }

        LocalDateTime inicioNuevo = request.getNuevaFechaCita().atTime(request.getNuevaHoraCita());
        if (!inicioNuevo.isAfter(Reloj.ahora())) {
            throw new IllegalArgumentException("La nueva fecha y hora deben ser posteriores al momento actual");
        }
        if (inicioNuevo.equals(inicioActual)) {
            throw new IllegalArgumentException("El nuevo horario es igual al horario actual de la cita");
        }

        // Las citas del Sprint 1 pueden no tener especialista: en ese caso no hay agenda que revisar.
        if (cita.getEspecialista() != null) {
            Long idEspecialista = cita.getEspecialista().getId();
            if (reglasAgenda.estaOcupado(idEspecialista, inicioNuevo)) {
                throw new IllegalArgumentException("El nuevo horario ya está ocupado por otra cita");
            }
            if (reglasAgenda.estaBloqueado(idEspecialista, inicioNuevo)) {
                throw new IllegalArgumentException("El nuevo horario está bloqueado por el especialista");
            }
        }

        ReprogramacionCita reprogramacion = new ReprogramacionCita();
        reprogramacion.setCita(cita);
        reprogramacion.setFechaAnterior(inicioActual.toLocalDate());
        reprogramacion.setHoraAnterior(inicioActual.toLocalTime());
        reprogramacion.setFechaNueva(request.getNuevaFechaCita());
        reprogramacion.setHoraNueva(request.getNuevaHoraCita());
        reprogramacion.setMotivo(request.getMotivo() == null || request.getMotivo().isBlank()
                ? null : request.getMotivo().trim());
        reprogramacion.setFechaRegistro(Reloj.ahora());

        cita.setFechaHora(inicioNuevo);
        citaRepository.save(cita);
        return reprogramacionCitaRepository.save(reprogramacion);
    }
}
