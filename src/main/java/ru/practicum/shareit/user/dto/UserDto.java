package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class UserDto {
    long id;

    @NotBlank(groups = OnCreate.class, message = "Параметр name не может быть пустым")
    private String name;

    @Email(message = "Некорректный формат email")
    @NotBlank(groups = OnCreate.class, message = "Параметр email не может быть пустым")
    private String email;
}
