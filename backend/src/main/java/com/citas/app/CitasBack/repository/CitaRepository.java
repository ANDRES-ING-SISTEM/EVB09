package com.citas.app.CitasBack.repository;

import com.citas.app.CitasBack.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByUsuarioId(Long usuarioId);

    List<Cita> findByUsuarioIdOrderByFechaHoraDesc(Long usuarioId);

    // ¿El horario tiene una cita que no esté cancelada?
    boolean existsByIdHorarioAndEstadoNot(Long idHorario, String estado);

    // ¿Este usuario ya tiene una cita activa en ese horario?
    boolean existsByUsuarioIdAndIdHorarioAndEstadoNot(Long usuarioId, Long idHorario, String estado);

    // ¿La especialidad tiene citas en alguno de esos estados? (HU-07)
    boolean existsByEspecialidadIdAndEstadoIn(Long especialidadId, Collection<String> estados);

    // De estos horarios, ¿cuáles tienen una cita activa?
    @Query("select c.idHorario from Cita c where c.idHorario in :ids and c.estado <> :estado")
    List<Long> idsHorariosOcupados(@Param("ids") Collection<Long> ids, @Param("estado") String estado);
}
