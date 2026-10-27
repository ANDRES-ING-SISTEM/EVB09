package com.citas.app.CitasBack.repository;

import com.citas.app.CitasBack.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByUsuarioId(Long usuarioId);

    // ---- Sprint 2 ----
    // Citas de un especialista cuyo inicio cae en [desde, hasta) y que no están en el estado indicado.
    List<Cita> findByEspecialistaIdAndFechaHoraGreaterThanEqualAndFechaHoraLessThanAndEstadoNot(
            Long especialistaId, LocalDateTime desde, LocalDateTime hasta, String estado);

    // ¿El especialista ya tiene una cita (distinta de la excluida) exactamente a esa fecha y hora?
    boolean existsByEspecialistaIdAndFechaHoraAndEstadoNot(
            Long especialistaId, LocalDateTime fechaHora, String estado);

    // ¿La especialidad tiene citas en alguno de esos estados? (HU-07)
    boolean existsByEspecialidadIdAndEstadoIn(Long especialidadId, Collection<String> estados);
}
