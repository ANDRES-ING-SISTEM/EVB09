package com.citas.app.CitasBack.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** HU-15: PUT /api/citas/{id}/reprogramar. */
@Getter
@Setter
public class ReprogramarRequest {

    @NotNull(message = "Debes elegir un nuevo horario.")
    private Long idHorario;
}
