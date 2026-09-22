package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.model.Disponibilidad;
import com.citas.app.CitasBack.service.DisponibilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilidades")
public class DisponibilidadController {

    @Autowired
    private DisponibilidadService disponibilidadService;

    @PostMapping("/especialista/{especialistaId}")
    public ResponseEntity<Disponibilidad> crear(@PathVariable Long especialistaId,
                                                  @RequestBody Disponibilidad disponibilidad) {
        return ResponseEntity.ok(disponibilidadService.crear(especialistaId, disponibilidad));
    }

    @GetMapping("/especialista/{especialistaId}")
    public ResponseEntity<List<Disponibilidad>> listar(@PathVariable Long especialistaId) {
        return ResponseEntity.ok(disponibilidadService.listarPorEspecialista(especialistaId));
    }
}
