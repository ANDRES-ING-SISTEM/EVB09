package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.dto.PerfilRequest;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/** HU-03: consultar y actualizar el perfil de un usuario. */
@Service
public class PerfilService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario consultar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un usuario con id " + id));
    }

    @Transactional
    public Usuario actualizar(Long id, PerfilRequest request) {
        Usuario usuario = consultar(id);
        String email = request.getEmail().trim();

        usuarioRepository.findByEmail(email)
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new IllegalArgumentException("Ya existe una cuenta registrada con ese correo");
                });

        usuario.setNombre(request.getNombre().trim());
        usuario.setApellido(request.getApellido().trim());
        usuario.setEmail(email);
        usuario.setTelefono(request.getTelefono() == null || request.getTelefono().isBlank()
                ? null : request.getTelefono().trim());
        return usuarioRepository.save(usuario);
    }
}
