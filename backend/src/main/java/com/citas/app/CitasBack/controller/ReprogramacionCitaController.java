package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.ReprogramarCitaRequest;
import com.citas.app.CitasBack.dto.ReprogramarCitaResponse;
import com.citas.app.CitasBack.model.ReprogramacionCita;
import com.citas.app.CitasBack.service.ReprogramacionCitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** HU-15: reprogramar cita. Convive con el PUT /api/citas/actualizar/{id} del Sprint 1. */
@RestController
@RequestMapping("/api/citas")
public class ReprogramacionCitaController {

    @Autowired
    private ReprogramacionCitaService reprogramacionCitaService;

    @PutMapping("/{idCita}/reprogramar")
    public ResponseEntity<ReprogramarCitaResponse> reprogramar(@PathVariable Long idCita,
                                                               @Valid @RequestBody ReprogramarCitaRequest request) {
        ReprogramacionCita reprogramacion = reprogramacionCitaService.reprogramar(idCita, request);
        return ResponseEntity.ok(new ReprogramarCitaResponse(reprogramacion.getCita(), reprogramacion));
    }
}
