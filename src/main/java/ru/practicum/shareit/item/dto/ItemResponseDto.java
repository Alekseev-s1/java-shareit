package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Getter
@Setter
public class ItemResponseDto {
    private long id;
    private String name;
    private String description;
    private UserDto owner;
    private Boolean available;
    private ItemRequestDto requestDto;
    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;
    private List<CommentResponseDto> comments;
}
