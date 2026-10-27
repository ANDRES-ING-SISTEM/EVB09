package com.citas.app.CitasBack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Respuestas de error uniformes para las historias del Sprint 2. Todas devuelven un JSON con la
 * clave "message", la misma que ya lee el frontend del Sprint 1 (err.response.data.message).
 * Las RuntimeException genéricas que lanzan los servicios del Sprint 1 NO se tocan.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> camposInvalidos(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));
        String primero = errores.values().stream().findFirst().orElse("Datos inválidos");
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("message", primero);
        cuerpo.put("errores", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> reglaDeNegocio(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", mensaje(ex)));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", mensaje(ex)));
    }

    private String mensaje(Exception ex) {
        return ex.getMessage() == null || ex.getMessage().isBlank() ? "Solicitud inválida" : ex.getMessage();
    }
}
