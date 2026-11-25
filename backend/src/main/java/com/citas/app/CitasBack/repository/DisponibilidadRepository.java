package com.citas.app.CitasBack.repository;

import com.citas.app.CitasBack.model.Disponibilidad;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    List<Disponibilidad> findByEspecialistaId(Long especialistaId);

    List<Disponibilidad> findByEspecialistaIdOrderByFechaAscHoraInicioAsc(Long especialistaId);

    List<Disponibilidad> findByEspecialistaIdAndFecha(Long especialistaId, LocalDate fecha);

    /** Bloquea la fila hasta terminar la transacción: evita dos reservas del mismo horario a la vez. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Disponibilidad d where d.id = :id")
    Optional<Disponibilidad> findByIdParaActualizar(@Param("id") Long id);
}
