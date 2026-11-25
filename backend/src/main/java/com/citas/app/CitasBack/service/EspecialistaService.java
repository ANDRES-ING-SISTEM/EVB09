package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.dto.EspecialistaRequest;
import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** HU-04: registrar especialistas. Un especialista es un Usuario con rol ESPECIALISTA. */
@Service
public class EspecialistaService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public EspecialistaService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrar(EspecialistaRequest r) {
        String correo = r.getCorreo().trim();
        if (usuarioRepository.existsByEmail(correo)) {
            throw ApiException.conflicto("Ya existe una cuenta registrada con ese correo.");
        }
        Usuario u = new Usuario();
        u.setNombre(r.getNombre().trim());
        u.setApellido(r.getApellido().trim());
        u.setEmail(correo);
        u.setPassword(passwordEncoder.encode(r.getContrasena()));
        u.setRol(Usuario.Rol.ESPECIALISTA);
        u.setTelefono(r.getTelefono() == null || r.getTelefono().isBlank() ? null : r.getTelefono().trim());
        u.setRegistroMedico(r.getRegistroMedico() == null || r.getRegistroMedico().isBlank()
                ? null : r.getRegistroMedico().trim());
        return usuarioRepository.save(u);
    }
}
