package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.dto.EspecialidadEdicionRequest;
import com.citas.app.CitasBack.model.Especialidad;
import com.citas.app.CitasBack.model.EstadoCita;
import com.citas.app.CitasBack.repository.CitaRepository;
import com.citas.app.CitasBack.repository.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/** HU-07: editar una especialidad (nombre, descripción y duración de la consulta). */
@Service
public class EspecialidadEdicionService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Transactional
    public Especialidad actualizar(Long idEspecialidad, EspecialidadEdicionRequest request) {
        Especialidad especialidad = especialidadRepository.findById(idEspecialidad)
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe una especialidad con id " + idEspecialidad));

        String nombre = request.getNombre().trim();
        if (especialidadRepository.existsByNombreAndIdNot(nombre, idEspecialidad)) {
            throw new IllegalArgumentException("Ya existe otra especialidad con ese nombre");
        }

        // No se puede cambiar la duración si ya hay citas pendientes de esta especialidad.
        Integer duracionActual = especialidad.getDuracionMinutos();
        boolean cambiaDuracion = duracionActual != null && !duracionActual.equals(request.getDuracionMinutos());
        if (cambiaDuracion && citaRepository.existsByEspecialidadIdAndEstadoIn(
                idEspecialidad, EstadoCita.PENDIENTES)) {
            throw new IllegalArgumentException(
                    "No se puede modificar la duración porque la especialidad tiene citas pendientes. "
                            + "Cancele o reprograme las citas antes de cambiar la duración.");
        }

        especialidad.setNombre(nombre);
        especialidad.setDescripcion(request.getDescripcion().trim());
        especialidad.setDuracionMinutos(request.getDuracionMinutos());
        return especialidadRepository.save(especialidad);
    }
}
