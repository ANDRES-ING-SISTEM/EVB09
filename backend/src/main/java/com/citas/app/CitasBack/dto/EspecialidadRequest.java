package com.citas.app.CitasBack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** HU-06: POST /api/especialidades. */
@Getter
@Setter
public class EspecialidadRequest {

    @NotBlank(message = "El nombre de la especialidad es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
    private String nombreEspecialidad;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres.")
    private String descripcion;

    @NotNull(message = "La duración es obligatoria.")
    @Min(value = 1, message = "La duración debe ser de al menos 1 minuto.")
    private Integer duracionMinutos;

    @NotNull(message = "Debes indicar el especialista.")
    private Long idEspecialista;
}
