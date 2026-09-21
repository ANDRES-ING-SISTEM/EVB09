package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import com.citas.app.CitasBack.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo ya se encuentra registrado en PostgreSQL");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Map<String, Object> login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(usuario.getEmail());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("usuario", Map.of(
            "id", usuario.getId(),
            "email", usuario.getEmail(),
            "nombre", usuario.getNombre(),
            "apellido", usuario.getApellido(),
            "rol", usuario.getRol().name()
        ));

        return result;
    }
}
