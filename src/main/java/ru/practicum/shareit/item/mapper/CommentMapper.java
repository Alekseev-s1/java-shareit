package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment dtoToComment(CommentRequestDto dto) {
        Comment comment = new Comment();

        comment.setText(dto.getText());
        return comment;
    }

    public static CommentResponseDto commentToDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String create = comment.getCreatedAt().format(formatter);

        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(LocalDateTime.parse(create));
        return dto;
    }
}
