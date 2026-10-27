package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.BloqueoMensajeResponse;
import com.citas.app.CitasBack.dto.BloqueoRequest;
import com.citas.app.CitasBack.dto.BloqueoResponse;
import com.citas.app.CitasBack.model.BloqueoHorario;
import com.citas.app.CitasBack.service.BloqueoHorarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** HU-11: bloquear y desbloquear horarios. */
@RestController
@RequestMapping("/api")
public class BloqueoHorarioController {

    @Autowired
    private BloqueoHorarioService bloqueoHorarioService;

    @PostMapping("/especialistas/{idEspecialista}/bloqueos")
    public ResponseEntity<BloqueoMensajeResponse> bloquear(@PathVariable Long idEspecialista,
                                                           @Valid @RequestBody BloqueoRequest request) {
        BloqueoHorario bloqueo = bloqueoHorarioService.bloquear(idEspecialista, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new BloqueoMensajeResponse("Horario bloqueado exitosamente", new BloqueoResponse(bloqueo)));
    }

    @GetMapping("/especialistas/{idEspecialista}/bloqueos")
    public ResponseEntity<List<BloqueoResponse>> listar(@PathVariable Long idEspecialista,
                                                        @RequestParam(required = false) String fecha) {
        return ResponseEntity.ok(bloqueoHorarioService.listarActivos(idEspecialista, fecha)
                .stream().map(BloqueoResponse::new).toList());
    }

    @DeleteMapping("/bloqueos/{idBloqueo}")
    public ResponseEntity<BloqueoMensajeResponse> desbloquear(@PathVariable Long idBloqueo) {
        BloqueoHorario bloqueo = bloqueoHorarioService.desbloquear(idBloqueo);
        return ResponseEntity.ok(
                new BloqueoMensajeResponse("Horario desbloqueado exitosamente", new BloqueoResponse(bloqueo)));
    }
}
