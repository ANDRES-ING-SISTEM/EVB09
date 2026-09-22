package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.model.Especialidad;
import com.citas.app.CitasBack.service.EspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    @PostMapping
    public ResponseEntity<Especialidad> registrar(@RequestBody Especialidad especialidad) {
        return ResponseEntity.ok(especialidadService.crear(especialidad));
    }

    @GetMapping
    public ResponseEntity<List<Especialidad>> listar() {
        return ResponseEntity.ok(especialidadService.listarActivas());
    }
}
