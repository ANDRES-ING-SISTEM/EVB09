package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.CancelarCitaRequest;
import com.citas.app.CitasBack.dto.CancelarCitaResponse;
import com.citas.app.CitasBack.model.Cita;
import com.citas.app.CitasBack.service.CancelacionCitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * HU-14: cancelar cita con validaciones. Convive con el DELETE /api/citas/cancelar/{id} del
 * Sprint 1 (CitaController), que sigue funcionando igual.
 */
@RestController
@RequestMapping("/api/citas")
public class CancelacionCitaController {

    @Autowired
    private CancelacionCitaService cancelacionCitaService;

    @PutMapping("/{idCita}/cancelar")
    public ResponseEntity<CancelarCitaResponse> cancelar(@PathVariable Long idCita,
                                                         @Valid @RequestBody CancelarCitaRequest request) {
        Cita cita = cancelacionCitaService.cancelar(idCita, request.getIdUsuario(), request.getMotivo());
        return ResponseEntity.ok(new CancelarCitaResponse(cita));
    }
}
