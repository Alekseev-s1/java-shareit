package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.UnitNotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    private UserDto owner;
    private ItemResponseDto itemResponseDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        owner = new UserDto();
        owner.setId(1);

        itemResponseDto = ItemResponseDto.builder()
                .id(1)
                .name("Test name")
                .description("Test description")
                .available(true)
                .owner(owner)
                .build();
    }

    @Test
    void getItemsByUserTest() throws Exception {
        Mockito
                .when(itemService.getItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemResponseDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemResponseDto.isAvailable())))
                .andExpect(jsonPath("$[0].owner.id", is(itemResponseDto.getOwner().getId()), Long.class));
    }

    @Test
    void getItemByIdTest() throws Exception {
        Mockito
                .when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName())))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponseDto.isAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemResponseDto.getOwner().getId()), Long.class));
    }

    @Test
    void getItemNotFoundTest() throws Exception {
        Mockito
                .when(itemService.getItem(anyLong(), anyLong()))
                .thenThrow(new UnitNotFoundException("Вещь с id = 1 не найдена"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Вещь с id = 1 не найдена")));

    }

    @Test
    void createItemTest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Test name");
        requestDto.setDescription("Test description");
        requestDto.setAvailable(true);

        Mockito
                .when(itemService.createItem(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName())))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponseDto.isAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemResponseDto.getOwner().getId()), Long.class));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Test name");
        requestDto.setDescription("Test description");
        requestDto.setAvailable(true);

        Mockito
                .when(itemService.updateItem(anyLong(), anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName())))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponseDto.isAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemResponseDto.getOwner().getId()), Long.class));
    }

    @Test
    void deleteItemTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/1"))
                .andExpect(status().isOk());
    }

    @Test
    void searchItemsTest() throws Exception {
        Mockito
                .when(itemService.searchItem(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemResponseDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemResponseDto.isAvailable())))
                .andExpect(jsonPath("$[0].owner.id", is(itemResponseDto.getOwner().getId()), Long.class));
    }

    @Test
    void addCommentTest() throws Exception {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Test text");

        User user = new User();
        user.setName("Test user name");

        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(1);
        commentResponseDto.setText("Test text");
        commentResponseDto.setAuthorName(user.getName());
        commentResponseDto.setCreated(LocalDateTime.now());

        Mockito
                .when(itemService.addComment(anyLong(), anyLong(), any(CommentRequestDto.class)))
                .thenReturn(commentResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponseDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentResponseDto.getCreated().format(formatter))));
    }

    @Test
    void addCommentByWrongOwnerTest() throws Exception {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Test text");

        Mockito
                .when(itemService.addComment(anyLong(), anyLong(), any(CommentRequestDto.class)))
                .thenThrow(new ItemUnavailableException("Пользователь с id = 1 не может оставить комментарий к вещи с id = 1, так как еще не бронировал ее"));

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Пользователь с id = 1 не может оставить комментарий к вещи с id = 1, так как еще не бронировал ее")));
    }
}
