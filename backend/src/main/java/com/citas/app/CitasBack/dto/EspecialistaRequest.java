package com.citas.app.CitasBack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** HU-04: POST /api/especialistas (solo administrador). Mismos nombres de campo que el frontend. */
@Getter
@Setter
public class EspecialistaRequest {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(max = 100, message = "El apellido no puede superar 100 caracteres.")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio.")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "El correo no tiene un formato válido.")
    private String correo;

    @Pattern(regexp = "^$|^[0-9+\\s-]{7,20}$", message = "El teléfono no tiene un formato válido.")
    private String telefono;

    @Size(max = 50, message = "El registro médico no puede superar 50 caracteres.")
    private String registroMedico;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String contrasena;
}
