package com.citas.app.CitasBack.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** HU-12: POST /api/citas. El paciente sale de la sesión. */
@Getter
@Setter
public class CitaRequest {

    @NotNull(message = "Debes elegir un horario.")
    private Long idHorario;

    @Size(max = 200, message = "El motivo no puede superar 200 caracteres.")
    private String motivo;
}
