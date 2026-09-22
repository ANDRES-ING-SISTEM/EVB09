package com.citas.app.CitasBack.repository;

import com.citas.app.CitasBack.model.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    List<Disponibilidad> findByEspecialistaId(Long especialistaId);
}
