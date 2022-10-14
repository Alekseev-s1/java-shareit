package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleItemNotFound(UnitNotFoundException e) {
        log.info(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        Map<String, String> map = new HashMap<>();
        map.put("error", e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleWrongOwner(WrongOwnerException e) {
        log.info(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        Map<String, String> map = new HashMap<>();
        map.put("error", e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleItemUnavailable(ItemUnavailableException e) {
        log.info(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        Map<String, String> map = new HashMap<>();
        map.put("error", e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleCrossDate(CrossDateException e) {
        log.error(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        Map<String, String> map = new HashMap<>();
        map.put("error", e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleStatusAlreadySet(StatusAlreadySetException e) {
        log.info(String.format("Ошибка %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        Map<String, String> map = new HashMap<>();
        map.put("error", e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
