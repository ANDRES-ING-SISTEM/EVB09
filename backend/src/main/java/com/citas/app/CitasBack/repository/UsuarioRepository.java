package com.citas.app.CitasBack.repository;

import com.citas.app.CitasBack.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);

    // Sprint 2 (HU-04): listar especialistas.
    List<Usuario> findByRol(Usuario.Rol rol);
}
