package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<String> handleUniqueEmail(UniqueEmailException e) {
        log.error(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleItemNotFound(ItemNotFoundException e) {
        log.error(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleWrongOwner(WrongOwnerException e) {
        log.error(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<List<String>> handleConstraintViolation(ConstraintViolationException e) {
        List<String> errors = new ArrayList<>();
        e.getConstraintViolations().forEach(error -> {
            log.error(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), error.getMessage()));
            String message = error.getMessage();
            errors.add(message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<List<String>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            log.error(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), error.getDefaultMessage()));
            String message = error.getDefaultMessage();
            errors.add(message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
