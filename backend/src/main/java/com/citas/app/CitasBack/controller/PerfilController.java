package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.PerfilRequest;
import com.citas.app.CitasBack.dto.UsuarioResponse;
import com.citas.app.CitasBack.service.PerfilService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** HU-03: gestión de perfil. El registro de usuarios sigue siendo HU-01 (POST /api/auth/registro). */
@RestController
@RequestMapping("/api/usuarios")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> consultar(@PathVariable Long id) {
        return ResponseEntity.ok(new UsuarioResponse(perfilService.consultar(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody PerfilRequest request) {
        return ResponseEntity.ok(new UsuarioResponse(perfilService.actualizar(id, request)));
    }
}
