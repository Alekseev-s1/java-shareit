package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class ItemDto {
    private long id;

    @NotBlank(groups = OnCreate.class, message = "Параметр name не может быть пустым")
    private String name;

    @NotBlank(groups = OnCreate.class, message = "Параметр description не может быть пустым")
    private String description;
    private UserDto owner;

    @NotNull(groups = OnCreate.class, message = "Параметр available не может быть пустым")
    private Boolean available;
}
