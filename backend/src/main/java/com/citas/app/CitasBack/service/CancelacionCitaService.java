package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.model.Cita;
import com.citas.app.CitasBack.model.EstadoCita;
import com.citas.app.CitasBack.repository.CitaRepository;
import com.citas.app.CitasBack.util.Reloj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/** HU-14: cancelar una cita pendiente. */
@Service
public class CancelacionCitaService {

    @Autowired
    private CitaRepository citaRepository;

    // Anticipación mínima (en horas). Se cambia en application.properties:
    // citas.cancelacion.horas-minimas=24
    @Value("${citas.cancelacion.horas-minimas:24}")
    private long horasMinimas;

    /**
     * La cita no se borra: pasa a estado CANCELADA. Las validaciones de horario ocupado (HU-10,
     * HU-12 y HU-15) ignoran las citas CANCELADA, así que ese horario queda libre de inmediato.
     */
    @Transactional
    public Cita cancelar(Long idCita, Long idUsuario, String motivo) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new NoSuchElementException("No existe una cita con id " + idCita));

        if (!idUsuario.equals(cita.getUsuario().getId())) {
            throw new IllegalArgumentException("La cita no pertenece al usuario indicado");
        }
        if (EstadoCita.CANCELADA.equals(cita.getEstado())) {
            throw new IllegalArgumentException("La cita ya fue cancelada");
        }
        if (!EstadoCita.admiteCambios(cita.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden cancelar citas pendientes");
        }
        LocalDateTime inicioCita = cita.getFechaHora();
        if (Reloj.ahora().plusHours(horasMinimas).isAfter(inicioCita)) {
            throw new IllegalArgumentException(
                    "Solo se puede cancelar con al menos " + horasMinimas + " horas de anticipación");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        cita.setMotivoCancelacion(motivo == null || motivo.isBlank() ? null : motivo.trim());
        cita.setFechaCancelacion(Reloj.ahora());
        return citaRepository.save(cita);
    }
}
