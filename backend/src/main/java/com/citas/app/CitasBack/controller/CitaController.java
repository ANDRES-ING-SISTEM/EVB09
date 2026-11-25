package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.CitaRequest;
import com.citas.app.CitasBack.dto.ReprogramarRequest;
import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.security.SesionActual;
import com.citas.app.CitasBack.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** HU-12 a HU-15. Solo pacientes; el usuario sale de la cookie, nunca de la URL ni del cuerpo. */
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;
    private final SesionActual sesionActual;

    public CitaController(CitaService citaService, SesionActual sesionActual) {
        this.citaService = citaService;
        this.sesionActual = sesionActual;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> solicitar(@Valid @RequestBody CitaRequest request) {
        Usuario paciente = sesionActual.exigirRol(Usuario.Rol.PACIENTE);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(citaService.solicitar(paciente, request.getIdHorario(), request.getMotivo()));
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Map<String, Object>> misCitas(@PathVariable Long idUsuario) {
        Usuario paciente = sesionActual.exigirRol(Usuario.Rol.PACIENTE);
        if (!paciente.getId().equals(idUsuario)) {
            throw ApiException.prohibido();
        }
        return citaService.listarDe(idUsuario);
    }

    @PutMapping("/{idCita}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long idCita) {
        Usuario paciente = sesionActual.exigirRol(Usuario.Rol.PACIENTE);
        citaService.cancelar(idCita, paciente);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idCita}/reprogramar")
    public Map<String, Object> reprogramar(@PathVariable Long idCita,
                                           @Valid @RequestBody ReprogramarRequest request) {
        Usuario paciente = sesionActual.exigirRol(Usuario.Rol.PACIENTE);
        return citaService.reprogramar(idCita, request.getIdHorario(), paciente);
    }
}
