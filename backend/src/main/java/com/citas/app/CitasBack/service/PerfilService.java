package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.dto.PerfilRequest;
import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** HU-03: consultar y actualizar el perfil propio. */
@Service
public class PerfilService {

    private final UsuarioRepository usuarioRepository;

    public PerfilService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Usuario consultar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> ApiException.noEncontrado("No existe un usuario con id " + id));
    }

    @Transactional
    public Usuario actualizar(Long id, PerfilRequest r) {
        Usuario u = consultar(id);
        String correo = r.getCorreo().trim();
        usuarioRepository.findByEmail(correo)
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw ApiException.campo("correo", "Ya existe una cuenta registrada con ese correo.");
                });
        u.setNombre(r.getNombre().trim());
        u.setApellido(r.getApellido().trim());
        u.setEmail(correo);
        u.setTelefono(r.getTelefono() == null || r.getTelefono().isBlank() ? null : r.getTelefono().trim());
        return usuarioRepository.save(u);
    }
}
