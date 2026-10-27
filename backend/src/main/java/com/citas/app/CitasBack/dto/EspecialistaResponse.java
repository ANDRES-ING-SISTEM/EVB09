package com.citas.app.CitasBack.dto;

import com.citas.app.CitasBack.model.Usuario;

public class EspecialistaResponse extends UsuarioResponse {

    private final String registroMedico;

    public EspecialistaResponse(Usuario especialista) {
        super(especialista);
        this.registroMedico = especialista.getRegistroMedico();
    }

    public String getRegistroMedico() { return registroMedico; }
}
