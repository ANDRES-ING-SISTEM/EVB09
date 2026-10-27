package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.dto.DisponibilidadLibreResponse;
import com.citas.app.CitasBack.model.Disponibilidad;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.DisponibilidadRepository;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import com.citas.app.CitasBack.util.Reloj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * HU-10: horarios de un especialista que de verdad se pueden reservar. Parte de las franjas que
 * el especialista definió en HU-09 (tabla disponibilidades) y descarta las que ya pasaron, las
 * que tienen una cita activa y las que se cruzan con un bloqueo de HU-11.
 */
@Service
public class ConsultaDisponibilidadService {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReglasAgenda reglasAgenda;

    public List<DisponibilidadLibreResponse> consultar(Long idEspecialista, LocalDate fecha) {
        Usuario especialista = usuarioRepository.findById(idEspecialista)
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe un especialista con id " + idEspecialista));
        if (especialista.getRol() != Usuario.Rol.ESPECIALISTA) {
            throw new IllegalArgumentException("El usuario indicado no es un especialista");
        }

        LocalDateTime ahora = Reloj.ahora();

        return disponibilidadRepository.findByEspecialistaId(idEspecialista).stream()
                .filter(d -> Boolean.TRUE.equals(d.getDisponible()))
                .filter(d -> fecha == null || d.getFecha().equals(fecha))
                .filter(d -> d.getFecha().atTime(d.getHoraInicio()).isAfter(ahora))
                .filter(d -> !reglasAgenda.hayCitaEnRango(idEspecialista,
                        d.getFecha().atTime(d.getHoraInicio()), d.getFecha().atTime(d.getHoraFin())))
                .filter(d -> !reglasAgenda.seCruzaConBloqueo(idEspecialista,
                        d.getFecha(), d.getHoraInicio(), d.getHoraFin()))
                .sorted(Comparator.comparing(Disponibilidad::getFecha)
                        .thenComparing(Disponibilidad::getHoraInicio))
                .map(d -> new DisponibilidadLibreResponse(
                        d.getId(), d.getFecha(), d.getHoraInicio(), d.getHoraFin()))
                .toList();
    }
}
