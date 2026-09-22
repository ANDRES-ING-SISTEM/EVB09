package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.model.Disponibilidad;
import com.citas.app.CitasBack.model.Usuario;
import com.citas.app.CitasBack.repository.DisponibilidadRepository;
import com.citas.app.CitasBack.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DisponibilidadService {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Disponibilidad crear(Long especialistaId, Disponibilidad datos) {
        Usuario especialista = usuarioRepository.findById(especialistaId)
                .orElseThrow(() -> new RuntimeException("Especialista no encontrado"));
        datos.setEspecialista(especialista);
        if (datos.getDisponible() == null) {
            datos.setDisponible(true);
        }
        return disponibilidadRepository.save(datos);
    }

    public List<Disponibilidad> listarPorEspecialista(Long especialistaId) {
        return disponibilidadRepository.findByEspecialistaId(especialistaId);
    }
}
