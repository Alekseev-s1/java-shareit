package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class UserDto {
    private long id;

    @NotBlank(groups = OnCreate.class, message = "Параметр name не может быть пустым")
    private String name;

    @Email(groups = {OnCreate.class, OnUpdate.class}, message = "Некорректный формат email")
    @NotBlank(groups = OnCreate.class, message = "Параметр email не может быть пустым")
    private String email;
}
