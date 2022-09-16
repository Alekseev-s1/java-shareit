package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.exception.UnitNotFoundException.unitNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    private User owner;
    private Item item;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);

        item = new Item();
        item.setId(1);
        item.setName("Test name");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setOwner(owner);
    }

    @Test
    void getItemsByUserTest() throws Exception {
        Mockito
                .when(itemService.getItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.isAvailable())))
                .andExpect(jsonPath("$[0].owner.id", is(item.getOwner().getId()), Long.class));
    }

    @Test
    void getItemByIdTest() throws Exception {
        Mockito
                .when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.isAvailable())))
                .andExpect(jsonPath("$.owner.id", is(item.getOwner().getId()), Long.class));
    }

    @Test
    void getItemNotFoundTest() throws Exception {
        Mockito
                .when(itemService.getItemById(anyLong()))
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
                .when(itemService.createItem(anyLong(), any(Item.class)))
                .thenReturn(item);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.isAvailable())))
                .andExpect(jsonPath("$.owner.id", is(item.getOwner().getId()), Long.class));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Test name");
        requestDto.setDescription("Test description");
        requestDto.setAvailable(true);

        Mockito
                .when(itemService.updateItem(anyLong(), anyLong(), any(Item.class)))
                .thenReturn(item);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.isAvailable())))
                .andExpect(jsonPath("$.owner.id", is(item.getOwner().getId()), Long.class));
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
                .thenReturn(List.of(item));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.isAvailable())))
                .andExpect(jsonPath("$[0].owner.id", is(item.getOwner().getId()), Long.class));
    }

    @Test
    void addCommentTest() throws Exception {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Test text");

        User user = new User();
        user.setName("Test user name");

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Test text");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreatedAt(LocalDateTime.now());

        Mockito
                .when(itemService.addComment(anyLong(), anyLong(), any(Comment.class)))
                .thenReturn(comment);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthor().getName())))
                .andExpect(jsonPath("$.created", is(comment.getCreatedAt().format(formatter))));
    }

    @Test
    void addCommentByWrongOwnerTest() throws Exception {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Test text");

        Mockito
                .when(itemService.addComment(anyLong(), anyLong(), any(Comment.class)))
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
