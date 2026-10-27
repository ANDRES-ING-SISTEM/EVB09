package com.citas.app.CitasBack.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CancelarCitaRequest {

    // Todavía no hay un filtro JWT que identifique al usuario en cada petición, así que el id de
    // quien cancela viaja en el body para verificar que la cita es suya.
    @NotNull(message = "Debe indicar qué usuario cancela la cita")
    private Long idUsuario;

    @Size(max = 200, message = "El motivo no puede superar los 200 caracteres")
    private String motivo;

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
