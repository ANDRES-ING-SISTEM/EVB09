package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.dto.EspecialidadRequest;
import com.citas.app.CitasBack.dto.EspecialidadUpdateRequest;
import com.citas.app.CitasBack.dto.Vista;
import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Especialidad;
import com.citas.app.CitasBack.model.EstadoCita;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.CitaRepository;
import com.citas.app.CitasBack.repository.EspecialidadRepository;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** HU-06, HU-07 y HU-08: especialidades, cada una con su especialista. */
@Service
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;

    public EspecialidadService(EspecialidadRepository especialidadRepository,
                               UsuarioRepository usuarioRepository,
                               CitaRepository citaRepository) {
        this.especialidadRepository = especialidadRepository;
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listar() {
        List<Map<String, Object>> salida = new ArrayList<>();
        for (Especialidad e : especialidadRepository.findByActivoTrue()) {
            salida.add(Vista.especialidad(e));
        }
        return salida;
    }

    @Transactional
    public Map<String, Object> crear(Usuario sesion, EspecialidadRequest r) {
        Usuario especialista = usuarioRepository.findById(r.getIdEspecialista())
                .filter(u -> u.getRol() == Usuario.Rol.ESPECIALISTA)
                .orElseThrow(() -> ApiException.noEncontrado("El especialista indicado no existe."));
        // Un especialista solo crea especialidades para sí mismo; el administrador, para cualquiera.
        if (sesion.getRol() == Usuario.Rol.ESPECIALISTA && !sesion.getId().equals(especialista.getId())) {
            throw ApiException.prohibido();
        }
        String nombre = r.getNombreEspecialidad().trim();
        if (especialidadRepository.existsByNombreIgnoreCaseAndEspecialistaId(nombre, especialista.getId())) {
            throw ApiException.conflicto("La especialidad ya se encuentra registrada.");
        }
        Especialidad e = new Especialidad();
        e.setNombre(nombre);
        e.setDescripcion(limpiar(r.getDescripcion()));
        e.setDuracionMinutos(r.getDuracionMinutos());
        e.setEspecialista(especialista);
        e.setActivo(true);
        return Vista.especialidad(especialidadRepository.save(e));
    }

    @Transactional
    public Map<String, Object> actualizar(Long id, Usuario sesion, EspecialidadUpdateRequest r) {
        Especialidad e = especialidadRepository.findById(id)
                .orElseThrow(() -> ApiException.noEncontrado("No existe una especialidad con id " + id));
        if (e.getEspecialista() == null || !e.getEspecialista().getId().equals(sesion.getId())) {
            throw ApiException.prohibido();
        }
        String nombre = r.getNombreEspecialidad().trim();
        if (especialidadRepository.existsByNombreIgnoreCaseAndEspecialistaIdAndIdNot(
                nombre, sesion.getId(), id)) {
            throw ApiException.conflicto("Ya tienes otra especialidad con ese nombre.");
        }
        boolean cambiaDuracion = !r.getDuracionMinutos().equals(e.getDuracionMinutos());
        if (cambiaDuracion && citaRepository.existsByEspecialidadIdAndEstadoIn(id, EstadoCita.PENDIENTES)) {
            throw ApiException.conflicto(
                    "No se puede cambiar la duración mientras haya citas pendientes de esta especialidad.");
        }
        e.setNombre(nombre);
        e.setDescripcion(limpiar(r.getDescripcion()));
        e.setDuracionMinutos(r.getDuracionMinutos());
        return Vista.especialidad(especialidadRepository.save(e));
    }

    private static String limpiar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
