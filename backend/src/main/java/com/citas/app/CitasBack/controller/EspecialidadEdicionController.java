package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.EspecialidadEdicionRequest;
import com.citas.app.CitasBack.model.Especialidad;
import com.citas.app.CitasBack.service.EspecialidadEdicionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** HU-07: editar especialidad. Convive con EspecialidadController (HU-06), que solo tiene POST y GET. */
@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadEdicionController {

    @Autowired
    private EspecialidadEdicionService especialidadEdicionService;

    @PutMapping("/{idEspecialidad}")
    public ResponseEntity<Especialidad> actualizar(@PathVariable Long idEspecialidad,
                                                   @Valid @RequestBody EspecialidadEdicionRequest request) {
        return ResponseEntity.ok(especialidadEdicionService.actualizar(idEspecialidad, request));
    }
}
