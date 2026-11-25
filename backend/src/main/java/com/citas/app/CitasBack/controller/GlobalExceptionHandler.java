package com.citas.app.CitasBack.controller;

import com.citas.app.CitasBack.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Formato de error que lee el frontend Vitalis: { "error": "texto" } para un mensaje general, o
 * { campo: "mensaje" } cuando el problema está en un campo concreto del formulario.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> apiException(ApiException ex) {
        if (ex.getCampos() != null) {
            return ResponseEntity.status(ex.getEstado()).body(ex.getCampos());
        }
        return ResponseEntity.status(ex.getEstado()).body(Map.of("error", mensaje(ex)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> camposInvalidos(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        if (errores.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Datos inválidos."));
        }
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> cuerpoIlegible(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Los datos enviados no tienen un formato válido."));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<Map<String, String>> parametroInvalido(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Algún parámetro de la solicitud no es válido."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> reglaDeNegocio(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", mensaje(ex)));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", mensaje(ex)));
    }

    private String mensaje(Exception ex) {
        return ex.getMessage() == null || ex.getMessage().isBlank() ? "Solicitud inválida." : ex.getMessage();
    }
}
