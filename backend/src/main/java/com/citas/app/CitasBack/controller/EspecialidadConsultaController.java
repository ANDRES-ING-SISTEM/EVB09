package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.Vista;
import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Especialidad;
import com.citas.app.CitasBack.repository.EspecialidadRepository;
import com.citas.app.CitasBack.security.SesionActual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** HU-20: consultar especialidades activas y el detalle de una especialidad. */
@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadConsultaController {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private SesionActual sesionActual;

    @GetMapping("/activas")
    public ResponseEntity<?> activas() {
        sesionActual.usuario();
        List<Map<String, Object>> lista = especialidadRepository.findByActivoTrue().stream().map(Vista::especialidad).toList();
        if (lista.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{idEspecialidad}/detalle")
    public Map<String, Object> detalle(@PathVariable Long idEspecialidad) {
        sesionActual.usuario();
        Especialidad e = especialidadRepository.findById(idEspecialidad)
                .orElseThrow(() -> ApiException.noEncontrado("No existe una especialidad con id " + idEspecialidad));
        Map<String, Object> r = new LinkedHashMap<>(Vista.especialidad(e));
        r.put("activo", e.getActivo());
        if (e.getEspecialista() != null) {
            r.put("nombreEspecialista", (e.getEspecialista().getNombre() + " " + e.getEspecialista().getApellido()).trim());
            r.put("correoEspecialista", e.getEspecialista().getEmail());
        }
        return r;
    }
}
