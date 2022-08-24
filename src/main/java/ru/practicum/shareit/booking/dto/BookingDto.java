package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDate;

@Setter
@Getter
public class BookingDto {
    private long id;
    private ItemDto item;
    private LocalDate start;
    private LocalDate end;
    private UserDto booker;
    private BookingStatus status;
}
