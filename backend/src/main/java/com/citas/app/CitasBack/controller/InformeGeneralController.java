package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Cita;
import com.citas.app.CitasBack.model.EstadoCita;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.CitaRepository;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import com.citas.app.CitasBack.security.SesionActual;
import com.citas.app.CitasBack.util.Reloj;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HU-19: informe general (solo administrador). El frontend manda ?desde=&hasta= (cada una opcional);
 * también se aceptan fechaInicio y fechaFin. Sin fechas, el informe abarca todo el historial.
 */
@RestController
@RequestMapping("/api/informes")
public class InformeGeneralController {

    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;
    private final SesionActual sesionActual;

    public InformeGeneralController(UsuarioRepository usuarioRepository,
                                    CitaRepository citaRepository,
                                    SesionActual sesionActual) {
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
        this.sesionActual = sesionActual;
    }

    @GetMapping("/general")
    @Transactional(readOnly = true)
    public Map<String, Object> general(@RequestParam(required = false) String desde,
                                       @RequestParam(required = false) String hasta,
                                       @RequestParam(required = false) String fechaInicio,
                                       @RequestParam(required = false) String fechaFin) {
        sesionActual.exigirRol(Usuario.Rol.ADMIN);

        final LocalDate ini = fecha(primero(desde, fechaInicio), "desde");
        final LocalDate fin = fecha(primero(hasta, fechaFin), "hasta");
        if (ini != null && fin != null && ini.isAfter(fin)) {
            throw ApiException.campo("hasta", "La fecha final no puede ser anterior a la fecha inicial.");
        }
        final boolean filtrado = ini != null || fin != null;

        long pacientes = usuarioRepository.findByRol(Usuario.Rol.PACIENTE).size();
        long especialistas = usuarioRepository.findByRol(Usuario.Rol.ESPECIALISTA).size();

        List<Cita> citas = citaRepository.findAll().stream()
                .filter(c -> {
                    LocalDate dia = c.getFechaHora().toLocalDate();
                    return (ini == null || !dia.isBefore(ini)) && (fin == null || !dia.isAfter(fin));
                })
                .toList();
        long programadas = citas.stream().filter(c -> EstadoCita.PENDIENTES.contains(c.getEstado())).count();
        long canceladas = citas.stream().filter(c -> EstadoCita.CANCELADA.equals(c.getEstado())).count();
        long realizadas = citas.stream().filter(c -> EstadoCita.COMPLETADA.equals(c.getEstado())).count();
        long total = programadas + canceladas + realizadas;

        String mensaje;
        if (filtrado && total == 0) {
            mensaje = "No hay datos de citas registrados para el periodo seleccionado.";
        } else if (!filtrado && pacientes == 0 && especialistas == 0 && total == 0) {
            mensaje = "No existen registros disponibles en la plataforma.";
        } else {
            mensaje = "Informe general generado exitosamente.";
        }

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("totalPacientes", pacientes);
        r.put("totalEspecialistas", especialistas);
        r.put("totalCitas", total);
        r.put("citasProgramadas", programadas);
        r.put("citasCanceladas", canceladas);
        r.put("citasRealizadas", realizadas);
        r.put("filtradoPorFechas", filtrado);
        r.put("desde", ini == null ? null : ini.toString());
        r.put("hasta", fin == null ? null : fin.toString());
        r.put("fechaGeneracion", Reloj.ahora().toString());
        r.put("mensaje", mensaje);
        return r;
    }

    private static String primero(String a, String b) {
        return a != null && !a.isBlank() ? a : b;
    }

    private static LocalDate fecha(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw ApiException.campo(campo, "La fecha no es válida (use AAAA-MM-DD).");
        }
    }
}
