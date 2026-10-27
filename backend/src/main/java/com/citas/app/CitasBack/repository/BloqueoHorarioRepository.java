package com.citas.app.CitasBack.repository;

import com.citas.app.CitasBack.model.BloqueoHorario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BloqueoHorarioRepository extends JpaRepository<BloqueoHorario, Long> {

    List<BloqueoHorario> findByEspecialistaIdAndFechaAndEstado(Long especialistaId, LocalDate fecha, String estado);

    List<BloqueoHorario> findByEspecialistaIdAndEstadoOrderByFechaAscHoraInicioAsc(Long especialistaId, String estado);
}
