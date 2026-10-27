package com.citas.app.CitasBack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // Sprint 2: la contraseña se puede recibir pero nunca se devuelve en el JSON.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // Sprint 2 (HU-03 / HU-04): datos opcionales, no afectan a los registros del Sprint 1.
    private String telefono;

    private String registroMedico;

    public enum Rol {
        PACIENTE,
        ESPECIALISTA,
        ADMIN
    }
}
