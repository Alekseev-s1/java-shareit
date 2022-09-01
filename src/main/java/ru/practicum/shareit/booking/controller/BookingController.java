package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return BookingMapper.bookingToDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponseDto> getBookings(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookings(state, userId).stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingByItemOwner(@RequestParam(defaultValue = "ALL") String state,
                                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingsByItemOwner(state, userId).stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestBody @Valid BookingRequestDto bookingDto) {
        return BookingMapper.bookingToDto(
                bookingService.createBooking(userId, BookingMapper.dtoToBooking(bookingDto))
        );
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto changeStatus(@PathVariable long bookingId,
                                           @RequestParam boolean approved,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return BookingMapper.bookingToDto(bookingService.changeStatus(bookingId, userId, approved));
    }


}
