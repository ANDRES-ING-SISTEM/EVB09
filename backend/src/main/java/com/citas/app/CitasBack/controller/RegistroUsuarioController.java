package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

// HU-01: registro con los campos del frontend Vitalis. Siempre crea rol PACIENTE (ROLE_USUARIO).
@RestController
@RequestMapping("/api/usuarios")
public class RegistroUsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> body) {
        String nombre = texto(body, "nombre");
        String apellido = texto(body, "apellido");
        String correo = texto(body, "correo");
        String contrasena = texto(body, "contrasena");
        String confirmar = texto(body, "confirmarContrasena");
        String telefono = texto(body, "telefono");

        Map<String, String> errores = new LinkedHashMap<>();
        if (nombre.isEmpty()) errores.put("nombre", "El nombre es obligatorio.");
        if (apellido.isEmpty()) errores.put("apellido", "El apellido es obligatorio.");
        if (!correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) errores.put("correo", "Ingresa un correo válido.");
        if (contrasena.length() < 8) errores.put("contrasena", "La contraseña debe tener al menos 8 caracteres.");
        if (!contrasena.equals(confirmar)) errores.put("confirmarContrasena", "Las contraseñas no coinciden.");
        if (errores.isEmpty() && usuarioRepository.existsByEmail(correo)) {
            errores.put("correo", "No pudimos completar el registro con ese correo.");
        }
        if (!errores.isEmpty()) return ResponseEntity.badRequest().body(errores);

        Usuario u = new Usuario();
        u.setEmail(correo);
        u.setPassword(passwordEncoder.encode(contrasena));
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setTelefono(telefono.isEmpty() ? null : telefono);
        u.setRol(Usuario.Rol.PACIENTE);
        u = usuarioRepository.save(u);

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("idUsuario", u.getId());
        r.put("nombre", u.getNombre());
        r.put("apellido", u.getApellido());
        r.put("correo", u.getEmail());
        r.put("telefono", u.getTelefono());
        return ResponseEntity.status(HttpStatus.CREATED).body(r);
    }

    private static String texto(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v == null ? "" : v.toString().trim();
    }
}
