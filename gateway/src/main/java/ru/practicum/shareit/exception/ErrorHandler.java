package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleConversionFailed(ConversionFailedException e) {
        log.error(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        Map<String, String> map = new HashMap<>();
        String message = "Unknown state: " + e.getValue();
        map.put("error", message);
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException e) {
        Map<String, String> map = new HashMap<>();
        e.getConstraintViolations().forEach(error -> {
            log.error(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), error.getMessage()));
            String message = error.getMessage();
            map.put("error", message);
        });
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> map = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            log.error(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), error.getDefaultMessage()));
            String message = error.getDefaultMessage();
            map.put("error", message);
        });
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
