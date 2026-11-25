package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.HorarioRequest;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.security.SesionActual;
import com.citas.app.CitasBack.service.HorarioService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** HU-09, HU-10 y HU-11. La modificación de un horario (HU-22) está en ModificarHorarioController. */
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;
    private final SesionActual sesionActual;

    public HorarioController(HorarioService horarioService, SesionActual sesionActual) {
        this.horarioService = horarioService;
        this.sesionActual = sesionActual;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody HorarioRequest request) {
        Usuario especialista = sesionActual.exigirRol(Usuario.Rol.ESPECIALISTA);
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioService.crear(especialista, request));
    }

    @GetMapping("/especialista/{idEspecialista}")
    public List<Map<String, Object>> deEspecialista(@PathVariable Long idEspecialista) {
        sesionActual.usuario();
        return horarioService.listarDeEspecialista(idEspecialista);
    }

    @GetMapping("/disponibles")
    public List<Map<String, Object>> disponibles(
            @RequestParam Long idEspecialidad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        sesionActual.usuario();
        return horarioService.disponibles(idEspecialidad, fecha);
    }

    @PutMapping("/{idHorario}/bloquear")
    public Map<String, Object> bloquear(@PathVariable Long idHorario) {
        Usuario especialista = sesionActual.exigirRol(Usuario.Rol.ESPECIALISTA);
        return horarioService.bloquear(idHorario, especialista);
    }

    @PutMapping("/{idHorario}/desbloquear")
    public Map<String, Object> desbloquear(@PathVariable Long idHorario) {
        Usuario especialista = sesionActual.exigirRol(Usuario.Rol.ESPECIALISTA);
        return horarioService.desbloquear(idHorario, especialista);
    }
}
