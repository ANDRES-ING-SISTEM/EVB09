package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.DisponibilidadLibreResponse;
import com.citas.app.CitasBack.service.ConsultaDisponibilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HU-10: consultar los horarios disponibles de un especialista. Convive con DisponibilidadController
 * (HU-09), que mantiene GET /api/disponibilidades/especialista/{id} con todas las franjas.
 */
@RestController
@RequestMapping("/api/disponibilidades")
public class DisponibilidadConsultaController {

    @Autowired
    private ConsultaDisponibilidadService consultaDisponibilidadService;

    @GetMapping("/especialista/{idEspecialista}/disponibles")
    public ResponseEntity<Map<String, Object>> consultar(
            @PathVariable Long idEspecialista,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<DisponibilidadLibreResponse> libres = consultaDisponibilidadService.consultar(idEspecialista, fecha);
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", libres.isEmpty()
                ? "El especialista no tiene horarios disponibles"
                : "Horarios disponibles encontrados exitosamente");
        cuerpo.put("data", libres);
        return ResponseEntity.ok(cuerpo);
    }
}
