package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CommentRequestDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}
