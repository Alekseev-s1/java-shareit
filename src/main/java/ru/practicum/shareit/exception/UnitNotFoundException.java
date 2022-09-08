package ru.practicum.shareit.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class UnitNotFoundException extends RuntimeException {
    public UnitNotFoundException(String message) {
        super(message);
    }

    public UnitNotFoundException(String message, long id) {
        super(MessageFormat.format(message, id));
    }

    public static Supplier<UnitNotFoundException> unitNotFoundException(String message) {
        return () -> new UnitNotFoundException(message);
    }

    public static Supplier<UnitNotFoundException> unitNotFoundException(String message, long id) {
        return () -> new UnitNotFoundException(message, id);
    }
}
