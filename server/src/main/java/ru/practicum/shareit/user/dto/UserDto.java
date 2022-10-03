package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private long id;
    private String name;
    private String email;
}
