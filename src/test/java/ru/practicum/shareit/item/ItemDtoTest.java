package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemResponseDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void itemJsonTest() throws IOException {
        ItemResponseDto.Booking nextBooking = new ItemResponseDto.Booking(1, 2);
        ItemResponseDto.Booking lastBooking = new ItemResponseDto.Booking(7, 7);

        UserDto userDto = new UserDto();
        userDto.setId(3);
        userDto.setName("Test user name");
        userDto.setEmail("test@user.com");

        CommentResponseDto firstCommentDto = new CommentResponseDto();
        firstCommentDto.setId(4);
        firstCommentDto.setText("Test text1");
        firstCommentDto.setAuthorName("First author name");
        firstCommentDto.setCreated(LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(formatter)));

        CommentResponseDto secondCommentDto = new CommentResponseDto();
        secondCommentDto.setId(5);
        secondCommentDto.setText("Test text2");
        secondCommentDto.setAuthorName("Second author name");
        secondCommentDto.setCreated(LocalDateTime.parse(LocalDateTime.now().minusDays(2).format(formatter)));

        ItemResponseDto itemDto = ItemResponseDto.builder()
                .id(6)
                .name("Test item name")
                .description("Test item description")
                .available(true)
                .owner(userDto)
                .comments(List.of(firstCommentDto, secondCommentDto))
                .nextBooking(nextBooking)
                .lastBooking(lastBooking)
                .build();

        JsonContent<ItemResponseDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) itemDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.isAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo((int) itemDto.getOwner().getId());
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo(itemDto.getOwner().getName());
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo(itemDto.getOwner().getEmail());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo((int) itemDto.getComments().get(0).getId());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(itemDto.getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo((itemDto.getComments().get(0).getAuthorName()));
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo((itemDto.getComments().get(0).getCreated().format(formatter)));
        assertThat(result).extractingJsonPathNumberValue("$.comments[1].id").isEqualTo((int) itemDto.getComments().get(1).getId());
        assertThat(result).extractingJsonPathStringValue("$.comments[1].text").isEqualTo(itemDto.getComments().get(1).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[1].authorName").isEqualTo((itemDto.getComments().get(1).getAuthorName()));
        assertThat(result).extractingJsonPathStringValue("$.comments[1].created").isEqualTo((itemDto.getComments().get(1).getCreated().format(formatter)));
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo((int) itemDto.getNextBooking().getId());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo((int) itemDto.getNextBooking().getBookerId());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo((int) itemDto.getLastBooking().getId());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo((int) itemDto.getLastBooking().getBookerId());
    }
}
