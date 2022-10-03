package ru.practicum.shareit.exception;

public class WrongBookingStateException extends RuntimeException {
    public WrongBookingStateException(String message) {
        super(message);
    }
}
