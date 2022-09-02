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

        booking.setId(bookingDto.getId());
        booking.setItem(item);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

    public static BookingResponseDto bookingToDto(Booking booking) {
        BookingResponseDto bookingDto = new BookingResponseDto();

        bookingDto.setId(booking.getId());
        bookingDto.setItem(new BookingResponseDto.Item(booking.getItem().getId(), booking.getItem().getName()));
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(new BookingResponseDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }
}
