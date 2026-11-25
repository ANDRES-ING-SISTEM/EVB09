package com.citas.app.CitasBack.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Error de negocio con su código HTTP. El GlobalExceptionHandler lo convierte en el JSON que lee
 * el frontend: { "error": "texto" } o, si es un error de campo, { campo: "mensaje" }.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus estado;
    private final Map<String, String> campos;

    public ApiException(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
        this.campos = null;
    }

    private ApiException(HttpStatus estado, String mensaje, Map<String, String> campos) {
        super(mensaje);
        this.estado = estado;
        this.campos = campos;
    }

    public static ApiException noAutenticado() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "Tu sesión no es válida o expiró.");
    }

    public static ApiException prohibido() {
        return new ApiException(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción.");
    }

    public static ApiException noEncontrado(String mensaje) {
        return new ApiException(HttpStatus.NOT_FOUND, mensaje);
    }

    public static ApiException conflicto(String mensaje) {
        return new ApiException(HttpStatus.CONFLICT, mensaje);
    }

    public static ApiException campo(String campo, String mensaje) {
        return new ApiException(HttpStatus.BAD_REQUEST, mensaje, Map.of(campo, mensaje));
    }

    public HttpStatus getEstado() {
        return estado;
    }

    public Map<String, String> getCampos() {
        return campos;
    }
}
