package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.WrongBookingStateException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState lookup(String state) {
        for (BookingState bookingState : values()) {
            if (bookingState.toString().equals(state)) {
                return bookingState;
            }
        }
        throw new WrongBookingStateException("Unknown state: " + state);
    }
}
