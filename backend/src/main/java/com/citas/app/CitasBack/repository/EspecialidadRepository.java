package com.citas.app.CitasBack.repository;

import com.citas.app.CitasBack.model.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    List<Especialidad> findByActivoTrue();
    boolean existsByNombre(String nombre);

    // Sprint 2 (HU-07): ¿otra especialidad distinta ya usa ese nombre?
    boolean existsByNombreAndIdNot(String nombre, Long id);
}
