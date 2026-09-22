package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.model.Cita;
import com.citas.app.CitasBack.service.CitaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping("/agendar/{usuarioId}")
    public ResponseEntity<Cita> agendarCita(@RequestBody Cita cita, @PathVariable Long usuarioId) {
        return ResponseEntity.ok(citaService.agendarCita(cita, usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Cita>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(citaService.obtenerCitasPorUsuario(usuarioId));
    }

    @PutMapping("/actualizar/{citaId}")
    public ResponseEntity<Cita> actualizarCita(@PathVariable Long citaId, @RequestBody Cita cita) {
        return ResponseEntity.ok(citaService.actualizarCita(citaId, cita));
    }

    @DeleteMapping("/cancelar/{citaId}")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long citaId) {
        citaService.cancelarCita(citaId);
        return ResponseEntity.noContent().build();
    }
}
