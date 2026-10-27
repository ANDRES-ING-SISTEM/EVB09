package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.EspecialistaRequest;
import com.citas.app.CitasBack.dto.EspecialistaResponse;
import com.citas.app.CitasBack.service.EspecialistaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** HU-04: registrar especialistas (y listarlos para HU-10 y HU-12). */
@RestController
@RequestMapping("/api/especialistas")
public class EspecialistaController {

    @Autowired
    private EspecialistaService especialistaService;

    @PostMapping
    public ResponseEntity<EspecialistaResponse> registrar(@Valid @RequestBody EspecialistaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new EspecialistaResponse(especialistaService.registrar(request)));
    }

    @GetMapping
    public ResponseEntity<List<EspecialistaResponse>> listar() {
        return ResponseEntity.ok(especialistaService.listar().stream()
                .map(EspecialistaResponse::new).toList());
    }
}
