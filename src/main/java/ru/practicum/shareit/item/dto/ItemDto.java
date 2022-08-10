package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.dto.UserDto;

@Setter
@Getter
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private UserDto owner;
    private boolean available;
}
