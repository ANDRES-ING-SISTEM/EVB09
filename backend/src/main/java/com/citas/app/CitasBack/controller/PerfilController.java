package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.dto.PerfilRequest;
import com.citas.app.CitasBack.dto.Vista;
import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.security.JwtAuthFilter;
import com.citas.app.CitasBack.security.JwtService;
import com.citas.app.CitasBack.security.SesionActual;
import com.citas.app.CitasBack.service.PerfilService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

/** HU-03: cada persona consulta y edita únicamente su propio perfil. */
@RestController
@RequestMapping("/api/usuarios")
public class PerfilController {

    private final PerfilService perfilService;
    private final SesionActual sesionActual;
    private final JwtService jwtService;

    public PerfilController(PerfilService perfilService, SesionActual sesionActual, JwtService jwtService) {
        this.perfilService = perfilService;
        this.sesionActual = sesionActual;
        this.jwtService = jwtService;
    }

    @GetMapping("/{idUsuario}")
    public Map<String, Object> consultar(@PathVariable Long idUsuario) {
        exigirPropio(idUsuario);
        return Vista.perfil(perfilService.consultar(idUsuario));
    }

    @PutMapping("/{idUsuario}")
    public Map<String, Object> actualizar(@PathVariable Long idUsuario,
                                          @Valid @RequestBody PerfilRequest request,
                                          HttpServletResponse response) {
        Usuario sesion = exigirPropio(idUsuario);
        boolean cambiaCorreo = !request.getCorreo().trim().equals(sesion.getEmail());
        Usuario actualizado = perfilService.actualizar(idUsuario, request);
        if (cambiaCorreo) {
            // El token guarda el correo: se emite uno nuevo para no cerrar la sesión de la persona.
            ResponseCookie cookie = ResponseCookie
                    .from(JwtAuthFilter.COOKIE, jwtService.generateToken(actualizado.getEmail()))
                    .httpOnly(true).secure(true).sameSite("None").path("/")
                    .maxAge(Duration.ofSeconds(jwtService.getExpirationSeconds())).build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        return Vista.perfil(actualizado);
    }

    private Usuario exigirPropio(Long idUsuario) {
        Usuario sesion = sesionActual.usuario();
        if (!sesion.getId().equals(idUsuario)) {
            throw ApiException.prohibido();
        }
        return sesion;
    }
}
