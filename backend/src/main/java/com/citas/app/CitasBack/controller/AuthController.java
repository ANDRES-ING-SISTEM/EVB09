package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import com.citas.app.CitasBack.security.JwtAuthFilter;
import com.citas.app.CitasBack.security.JwtService;
import com.citas.app.CitasBack.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(authService.registrar(usuario));
    }

    // Acepta correo/contrasena (frontend Vitalis) y email/password (Postman de sprints anteriores).
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> c) {
        String email = c.getOrDefault("correo", c.get("email"));
        String clave = c.getOrDefault("contrasena", c.get("password"));
        if (email == null || clave == null || email.isBlank() || clave.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Usuario> u = usuarioRepository.findByEmail(email.trim());
        if (u.isEmpty() || !passwordEncoder.matches(clave, u.get().getPassword())) {
            return ResponseEntity.status(401).build();
        }
        String token = jwtService.generateToken(u.get().getEmail());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie(token, Duration.ofSeconds(jwtService.getExpirationSeconds())).toString())
                .body(sesion(u.get()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        return usuarioRepository.findByEmail(auth.getName())
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(sesion(u)))
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie("", Duration.ZERO).toString())
                .build();
    }

    private static Map<String, Object> sesion(Usuario u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idUsuario", u.getId());
        m.put("nombre", u.getNombre());
        m.put("rol", JwtAuthFilter.rolFrontend(u.getRol()));
        return m;
    }

    // Frontend y backend están en dominios distintos de Render: Secure + SameSite=None.
    private static ResponseCookie cookie(String valor, Duration duracion) {
        return ResponseCookie.from(JwtAuthFilter.COOKIE, valor)
                .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(duracion).build();
    }
}
