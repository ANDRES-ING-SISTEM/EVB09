package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.dto.EspecialistaRequest;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * HU-04: registrar especialistas. En el modelo del Sprint 1 un especialista es un Usuario con rol
 * ESPECIALISTA (tabla usuarios), así que inicia sesión con el mismo login (HU-02).
 */
@Service
public class EspecialistaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrar(EspecialistaRequest request) {
        if (!request.getPassword().equals(request.getConfirmarPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        String email = request.getEmail().trim();
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Ya existe una cuenta registrada con ese correo");
        }

        Usuario especialista = new Usuario();
        especialista.setNombre(request.getNombre().trim());
        especialista.setApellido(request.getApellido().trim());
        especialista.setEmail(email);
        especialista.setPassword(passwordEncoder.encode(request.getPassword()));
        especialista.setRol(Usuario.Rol.ESPECIALISTA);
        especialista.setTelefono(request.getTelefono() == null || request.getTelefono().isBlank()
                ? null : request.getTelefono().trim());
        especialista.setRegistroMedico(request.getRegistroMedico().trim());
        return usuarioRepository.save(especialista);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findByRol(Usuario.Rol.ESPECIALISTA);
    }
}
