package com.citas.app.CitasBack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** HU-03: PUT /api/usuarios/{id}. */
@Getter
@Setter
public class PerfilRequest {

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
}
