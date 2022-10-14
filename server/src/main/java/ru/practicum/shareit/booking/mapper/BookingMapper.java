package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

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
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(new BookingResponseDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()))
                .item(new BookingResponseDto.Item(booking.getItem().getId(), booking.getItem().getName()))
                .status(booking.getStatus())
                .build();
    }
}
