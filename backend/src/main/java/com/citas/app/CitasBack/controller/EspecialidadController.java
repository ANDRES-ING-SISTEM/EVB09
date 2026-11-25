package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.EspecialidadRequest;
import com.citas.app.CitasBack.dto.EspecialidadUpdateRequest;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.security.SesionActual;
import com.citas.app.CitasBack.service.EspecialidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** HU-06, HU-07 y HU-08. El listado es público; crear y editar exigen sesión con rol. */
@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    private final EspecialidadService especialidadService;
    private final SesionActual sesionActual;

    public EspecialidadController(EspecialidadService especialidadService, SesionActual sesionActual) {
        this.especialidadService = especialidadService;
        this.sesionActual = sesionActual;
    }

    @GetMapping
    public List<Map<String, Object>> listar() {
        return especialidadService.listar();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody EspecialidadRequest request) {
        Usuario sesion = sesionActual.exigirRol(Usuario.Rol.ESPECIALISTA, Usuario.Rol.ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).body(especialidadService.crear(sesion, request));
    }

    @PutMapping("/{idEspecialidad}")
    public Map<String, Object> actualizar(@PathVariable Long idEspecialidad,
                                          @Valid @RequestBody EspecialidadUpdateRequest request) {
        Usuario sesion = sesionActual.exigirRol(Usuario.Rol.ESPECIALISTA);
        return especialidadService.actualizar(idEspecialidad, sesion, request);
    }
}
