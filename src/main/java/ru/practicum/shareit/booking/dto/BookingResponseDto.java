package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class BookingResponseDto {
    private long id;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private Booker booker;
    private BookingStatus status;

    @Data
    public static class Booker {
        private final long id;
        private final String name;
    }

    @Data
    public static class Item {
        private final long id;
        private final String name;
    }
}
