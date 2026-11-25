package com.citas.app.CitasBack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "especialidades",
        uniqueConstraints = @UniqueConstraint(columnNames = {"nombre", "especialista_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    // Duración de la consulta en minutos (HU-06 / HU-07).
    private Integer duracionMinutos;

    // Especialista que atiende esta especialidad (el frontend la crea con idEspecialista).
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "especialista_id")
    private Usuario especialista;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
