package ru.practicum.shareit.request;

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
import ru.practicum.shareit.requests.dto.ItemReqRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

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
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private User requestor;
    private ItemRequest itemRequest;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setId(1);

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Test description");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        Mockito
                .when(itemRequestService.getItemRequestById(anyLong()))
                .thenReturn(itemRequest);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequest.getCreated().format(formatter))));
    }

    @Test
    void getItemRequestByUserTest() throws Exception {
        Mockito
                .when(itemRequestService.getItemRequestsByUser(anyLong()))
                .thenReturn(List.of(itemRequest));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequest.getCreated().format(formatter))));
    }

    @Test
    void getItemRequestsTest() throws Exception {
        Mockito
                .when(itemRequestService.getItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequest));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequest.getCreated().format(formatter))));
    }

    @Test
    void createItemRequestTest() throws Exception {
        ItemReqRequestDto reqRequestDto = new ItemReqRequestDto();
        reqRequestDto.setDescription("Test description");

        Mockito
                .when(itemRequestService.createItemRequest(anyLong(), any(ItemRequest.class)))
                .thenReturn(itemRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(reqRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequest.getCreated().format(formatter))));
    }
}
