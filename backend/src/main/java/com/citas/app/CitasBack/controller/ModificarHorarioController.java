package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.ModificarHorarioApiRequest;
import com.citas.app.CitasBack.dto.Vista;
import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Disponibilidad;
import com.citas.app.CitasBack.model.EstadoCita;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.CitaRepository;
import com.citas.app.CitasBack.repository.DisponibilidadRepository;
import com.citas.app.CitasBack.security.SesionActual;
import com.citas.app.CitasBack.util.Reloj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

/** HU-22: modificar un horario propio que no tenga citas reservadas. */
@RestController
@RequestMapping("/api/horarios")
public class ModificarHorarioController {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private SesionActual sesionActual;

    @PutMapping("/{idHorario}")
    @Transactional
    public Map<String, Object> modificar(@PathVariable Long idHorario,
                                         @RequestBody ModificarHorarioApiRequest request) {
        Usuario especialista = sesionActual.exigirRol(Usuario.Rol.ESPECIALISTA);
        Disponibilidad d = disponibilidadRepository.findByIdParaActualizar(idHorario)
                .orElseThrow(() -> ApiException.noEncontrado("No se encontró el horario con ID: " + idHorario));
        if (!d.getEspecialista().getId().equals(especialista.getId())) {
            throw ApiException.prohibido();
        }
        if (citaRepository.existsByIdHorarioAndEstadoNot(idHorario, EstadoCita.CANCELADA)) {
            throw ApiException.conflicto("Advertencia de impacto: el horario tiene una cita reservada. "
                    + "No se permite la modificación hasta que la cita sea cancelada o reprogramada.");
        }
        LocalDate fecha = request.getFecha() != null ? request.getFecha()
                : (request.getFechaEspecifica() != null ? request.getFechaEspecifica() : d.getFecha());
        LocalTime inicio = request.getHoraInicio() != null ? request.getHoraInicio() : d.getHoraInicio();
        LocalTime fin = request.getHoraFin() != null ? request.getHoraFin() : d.getHoraFin();
        if (!fin.isAfter(inicio)) {
            throw ApiException.campo("horaFin", "La hora de fin debe ser posterior a la hora de inicio.");
        }
        if (!fecha.atTime(inicio).isAfter(Reloj.ahora())) {
            throw ApiException.campo("fecha", "El horario debe ser posterior al momento actual.");
        }
        for (Disponibilidad otro : disponibilidadRepository.findByEspecialistaIdAndFecha(especialista.getId(), fecha)) {
            if (!otro.getId().equals(idHorario) && inicio.isBefore(otro.getHoraFin()) && otro.getHoraInicio().isBefore(fin)) {
                throw ApiException.conflicto("El horario indicado se solapa con otro bloque configurado ("
                        + otro.getHoraInicio() + " - " + otro.getHoraFin() + ").");
            }
        }
        d.setFecha(fecha);
        d.setHoraInicio(inicio);
        d.setHoraFin(fin);
        d = disponibilidadRepository.save(d);

        Map<String, Object> r = new LinkedHashMap<>(Vista.horario(d, Boolean.FALSE.equals(d.getDisponible()) ? "BLOQUEADO" : "LIBRE"));
        r.put("idEspecialista", especialista.getId());
        r.put("nombreEspecialista", (especialista.getNombre() + " " + especialista.getApellido()).trim());
        r.put("disponibilidadActualizada", true);
        r.put("mensaje", "Horario modificado exitosamente y disponibilidad actualizada.");
        return r;
    }
}
