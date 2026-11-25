package com.citas.app.CitasBack.config;

import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea el primer administrador al arrancar, solo si ADMIN_EMAIL y ADMIN_PASSWORD están definidas y
 * ese correo no existe. Sin administrador nadie puede registrar especialistas (HU-04).
 */
@Component
public class AdminInicial implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicial.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String email;

    @Value("${app.admin.password:}")
    private String password;

    public AdminInicial(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return;
        }
        String correo = email.trim();
        if (password.length() < 8) {
            log.warn("ADMIN_PASSWORD tiene menos de 8 caracteres: no se crea el administrador inicial.");
            return;
        }
        if (usuarioRepository.existsByEmail(correo)) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setNombre("Administrador");
        admin.setApellido("Vitalis");
        admin.setEmail(correo);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRol(Usuario.Rol.ADMIN);
        usuarioRepository.save(admin);
        log.info("Administrador inicial creado: {}", correo);
    }
}
