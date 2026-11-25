package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.EspecialistaEdicionRequest;
import com.citas.app.CitasBack.dto.EspecialistaRequest;
import com.citas.app.CitasBack.dto.Vista;
import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Especialidad;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.EspecialidadRepository;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import com.citas.app.CitasBack.security.JwtAuthFilter;
import com.citas.app.CitasBack.security.JwtService;
import com.citas.app.CitasBack.security.SesionActual;
import com.citas.app.CitasBack.service.EspecialistaService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HU-04, HU-20 y HU-21. Contrato del frontend:
 *   GET  /api/especialistas       lista con sus especialidades (cualquier sesión)
 *   GET  /api/especialistas/{id}  perfil de un especialista (cualquier sesión)
 *   POST /api/especialistas       registrar (solo administrador)
 *   PUT  /api/especialistas/{id}  editar (administrador, o el propio especialista)
 */
@RestController
@RequestMapping("/api/especialistas")
public class EspecialistaConsultaController {

    private final UsuarioRepository usuarioRepository;
    private final EspecialidadRepository especialidadRepository;
    private final EspecialistaService especialistaService;
    private final SesionActual sesionActual;
    private final JwtService jwtService;

    public EspecialistaConsultaController(UsuarioRepository usuarioRepository,
                                          EspecialidadRepository especialidadRepository,
                                          EspecialistaService especialistaService,
                                          SesionActual sesionActual,
                                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.especialidadRepository = especialidadRepository;
        this.especialistaService = especialistaService;
        this.sesionActual = sesionActual;
        this.jwtService = jwtService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listar() {
        sesionActual.usuario();
        Map<Long, List<Especialidad>> porEspecialista = new HashMap<>();
        for (Especialidad e : especialidadRepository.findByActivoTrue()) {
            if (e.getEspecialista() != null) {
                porEspecialista.computeIfAbsent(e.getEspecialista().getId(), k -> new ArrayList<>()).add(e);
            }
        }
        List<Map<String, Object>> salida = new ArrayList<>();
        for (Usuario u : usuarioRepository.findByRol(Usuario.Rol.ESPECIALISTA)) {
            salida.add(detalle(u, porEspecialista.getOrDefault(u.getId(), List.of())));
        }
        return salida;
    }

    @GetMapping("/{idUsuario}")
    @Transactional(readOnly = true)
    public Map<String, Object> consultar(@PathVariable Long idUsuario) {
        sesionActual.usuario();
        Usuario e = especialista(idUsuario);
        return detalle(e, especialidadRepository.findByEspecialistaIdAndActivoTrueOrderByIdAsc(idUsuario));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(@Valid @RequestBody EspecialistaRequest request) {
        sesionActual.exigirRol(Usuario.Rol.ADMIN);
        Usuario creado = especialistaService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(detalle(creado, List.of()));
    }

    @PutMapping("/{idUsuario}")
    @Transactional
    public Map<String, Object> editar(@PathVariable Long idUsuario,
                                      @Valid @RequestBody EspecialistaEdicionRequest request,
                                      HttpServletResponse response) {
        Usuario sesion = sesionActual.exigirRol(Usuario.Rol.ADMIN, Usuario.Rol.ESPECIALISTA);
        if (sesion.getRol() == Usuario.Rol.ESPECIALISTA && !sesion.getId().equals(idUsuario)) {
            throw ApiException.prohibido();
        }
        Usuario e = especialista(idUsuario);
        String correo = request.getCorreo().trim();
        usuarioRepository.findByEmail(correo)
                .filter(otro -> !otro.getId().equals(idUsuario))
                .ifPresent(otro -> {
                    throw ApiException.conflicto("Ya existe una cuenta registrada con ese correo.");
                });
        boolean cambiaCorreo = !correo.equals(e.getEmail());
        e.setNombre(request.getNombre().trim());
        e.setApellido(request.getApellido().trim());
        e.setEmail(correo);
        e.setTelefono(request.getTelefono() == null || request.getTelefono().isBlank()
                ? null : request.getTelefono().trim());
        e.setRegistroMedico(request.getRegistroMedico() == null || request.getRegistroMedico().isBlank()
                ? null : request.getRegistroMedico().trim());
        e = usuarioRepository.save(e);

        if (cambiaCorreo && sesion.getId().equals(idUsuario)) {
            ResponseCookie cookie = ResponseCookie
                    .from(JwtAuthFilter.COOKIE, jwtService.generateToken(e.getEmail()))
                    .httpOnly(true).secure(true).sameSite("None").path("/")
                    .maxAge(Duration.ofSeconds(jwtService.getExpirationSeconds())).build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        return detalle(e, especialidadRepository.findByEspecialistaIdAndActivoTrueOrderByIdAsc(idUsuario));
    }

    private Usuario especialista(Long id) {
        return usuarioRepository.findById(id)
                .filter(u -> u.getRol() == Usuario.Rol.ESPECIALISTA)
                .orElseThrow(() -> ApiException.noEncontrado("No existe un especialista con id " + id));
    }

    private static Map<String, Object> detalle(Usuario u, List<Especialidad> especialidades) {
        Map<String, Object> m = new LinkedHashMap<>(Vista.perfil(u));
        m.put("registroMedico", u.getRegistroMedico());
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Especialidad e : especialidades) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("idEspecialidad", e.getId());
            item.put("nombreEspecialidad", e.getNombre());
            lista.add(item);
        }
        m.put("especialidades", lista);
        return m;
    }
}
