package com.citas.app.CitasBack.security;

import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String COOKIE = "token";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /** El frontend usa ROLE_USUARIO / ROLE_ESPECIALISTA / ROLE_ADMIN; la base guarda PACIENTE / ESPECIALISTA / ADMIN. */
    public static String rolFrontend(Usuario.Rol rol) {
        return rol == Usuario.Rol.PACIENTE ? "ROLE_USUARIO" : "ROLE_" + rol.name();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (COOKIE.equals(c.getName())) token = c.getValue();
            }
        }
        if (token != null && jwtService.isValid(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            usuarioRepository.findByEmail(jwtService.extractUsername(token)).ifPresent(u -> {
                var auth = new UsernamePasswordAuthenticationToken(
                        u.getEmail(), null, List.of(new SimpleGrantedAuthority(rolFrontend(u.getRol()))));
                SecurityContextHolder.getContext().setAuthentication(auth);
            });
        }
        chain.doFilter(request, response);
    }
}
