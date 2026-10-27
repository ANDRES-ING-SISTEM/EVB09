package com.citas.app.CitasBack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "especialidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    // Sprint 2 (HU-07): duración de la consulta en minutos. Opcional para no afectar al Sprint 1.
    private Integer duracionMinutos;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
