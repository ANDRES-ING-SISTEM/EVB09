package com.citas.app.CitasBack.security;

import com.citas.app.CitasBack.exception.ApiException;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Quién hace la petición. La identidad sale SIEMPRE de la cookie JWT (el filtro la deja en el
 * contexto de seguridad) y el rol se lee de la base de datos; nunca de lo que mande el cliente.
 */
@Component
public class SesionActual {

    private final UsuarioRepository usuarioRepository;

    public SesionActual(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Usuario de la sesión. Sin sesión válida responde 401. */
    public Usuario usuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken || auth.getName() == null) {
            throw ApiException.noAutenticado();
        }
        return usuarioRepository.findByEmail(auth.getName()).orElseThrow(ApiException::noAutenticado);
    }

    /** Usuario de la sesión, que además debe tener alguno de los roles indicados (si no, 403). */
    public Usuario exigirRol(Usuario.Rol... roles) {
        Usuario u = usuario();
        for (Usuario.Rol r : roles) {
            if (u.getRol() == r) {
                return u;
            }
        }
        throw ApiException.prohibido();
    }
}
