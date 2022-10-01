package ru.practicum.shareit.requests.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class ItemReqRequestDto {
    @NotBlank(message = "Параметр description не может быть пустым")
    private String description;
}
