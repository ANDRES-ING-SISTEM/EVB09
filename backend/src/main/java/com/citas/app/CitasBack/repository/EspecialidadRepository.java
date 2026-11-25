package com.citas.app.CitasBack.repository;

import com.citas.app.CitasBack.model.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

    List<Especialidad> findByActivoTrue();

    List<Especialidad> findByEspecialistaIdAndActivoTrueOrderByIdAsc(Long especialistaId);

    Optional<Especialidad> findFirstByEspecialistaIdAndActivoTrueOrderByIdAsc(Long especialistaId);

    boolean existsByNombreIgnoreCaseAndEspecialistaId(String nombre, Long especialistaId);

    boolean existsByNombreIgnoreCaseAndEspecialistaIdAndIdNot(String nombre, Long especialistaId, Long id);
}
