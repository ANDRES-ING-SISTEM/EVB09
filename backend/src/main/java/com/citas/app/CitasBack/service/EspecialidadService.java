package com.citas.app.CitasBack.service;

import com.citas.app.CitasBack.model.Especialidad;
import com.citas.app.CitasBack.repository.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    public Especialidad crear(Especialidad especialidad) {
        if (especialidadRepository.existsByNombre(especialidad.getNombre())) {
            throw new RuntimeException("La especialidad ya se encuentra registrada");
        }
        if (especialidad.getActivo() == null) {
            especialidad.setActivo(true);
        }
        return especialidadRepository.save(especialidad);
    }

    public List<Especialidad> listarActivas() {
        return especialidadRepository.findByActivoTrue();
    }
}
