package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.security.SesionActual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HU-24: control de acceso por rol. La regla se aplica en el servidor: la sesión (cookie JWT) identifica
 * a la persona, el rol se lee de la base de datos y cada endpoint exige el suyo (sin sesión 401, sin permiso 403).
 */
@RestController
@RequestMapping("/api/acceso")
public class AccesoController {

    @Autowired
    private SesionActual sesionActual;

    @GetMapping("/mi-rol")
    public Map<String, Object> miRol() {
        Usuario u = sesionActual.usuario();
        String rol;
        List<String> permisos;
        if (u.getRol() == Usuario.Rol.ADMIN) {
            rol = "ADMINISTRADOR";
            permisos = List.of("GET /api/informes/general", "POST /api/especialistas",
                        "PUT /api/especialistas/{id}", "GET /api/especialistas", 
                        "GET /api/especialistas/{id}", "POST /api/especialidades",
                        "PUT /api/especialidades/{id}", "GET /api/acceso/mi-rol");
        } else if (u.getRol() == Usuario.Rol.ESPECIALISTA) {
            rol = "ESPECIALISTA";
            permisos = List.of("POST /api/horarios", "PUT /api/horarios/{id}",
                        "PUT /api/horarios/{id}/bloquear", "PUT /api/horarios/{id}/desbloquear",
                        "GET /api/horarios/especialista/{id}", "POST /api/especialidades",
                        "PUT /api/especialidades/{id}", "PUT /api/especialistas/{id} (solo el suyo)",
                        "GET /api/acceso/mi-rol");
        } else {
            rol = "CLIENTE";
            permisos = List.of("POST /api/citas", "GET /api/citas/usuario/{id} (solo el suyo)",
                        "PUT /api/citas/{id}/cancelar", "PUT /api/citas/{id}/reprogramar",
                        "GET /api/horarios/disponibles", "GET /api/acceso/mi-rol");
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("idUsuario", u.getId());
        r.put("rol", rol);
        r.put("permisos", permisos);
        return r;
    }
}
