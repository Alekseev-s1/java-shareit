package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking dtoToBooking(BookingRequestDto bookingDto) {
        Booking booking = new Booking();
        Item item = new Item();
        item.setId(bookingDto.getItemId());

        booking.setItem(item);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

    public static BookingResponseDto bookingToDto(Booking booking) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String start = booking.getStart().format(formatter);
        String end = booking.getEnd().format(formatter);

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(LocalDateTime.parse(start))
                .end(LocalDateTime.parse(end))
                .booker(new BookingResponseDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()))
                .item(new BookingResponseDto.Item(booking.getItem().getId(), booking.getItem().getName()))
                .status(booking.getStatus())
                .build();
    }
}
