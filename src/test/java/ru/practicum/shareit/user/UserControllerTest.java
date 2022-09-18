package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private UserDto firstUserDto;

    @BeforeEach
    void setUp() {
        firstUserDto = new UserDto();
        firstUserDto.setId(1);
        firstUserDto.setName("First name");
        firstUserDto.setEmail("first@test.com");
    }

    @Test
    void getUsersTest() throws Exception {
        UserDto secondUserDto = new UserDto();
        secondUserDto.setId(2);
        secondUserDto.setName("Second name");
        secondUserDto.setEmail("second@test.com");

        Mockito
                .when(userService.getUsers())
                .thenReturn(List.of(firstUserDto, secondUserDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(firstUserDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(firstUserDto.getName())))
                .andExpect(jsonPath("$[0].email", is(firstUserDto.getEmail())))
                .andExpect(jsonPath("$[1].id", is(secondUserDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(secondUserDto.getName())))
                .andExpect(jsonPath("$[1].email", is(secondUserDto.getEmail())));
    }

    @Test
    void getUserByIdTest() throws Exception {
        Mockito
                .when(userService.getUser(anyLong()))
                .thenReturn(firstUserDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUserDto.getName())))
                .andExpect(jsonPath("$.email", is(firstUserDto.getEmail())));
    }

    @Test
    void createUserTest() throws Exception {
        Mockito
                .when(userService.createUser(any(UserDto.class)))
                .thenReturn(this.firstUserDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(objectMapper.writeValueAsString(firstUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(this.firstUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(this.firstUserDto.getName())))
                .andExpect(jsonPath("$.email", is(this.firstUserDto.getEmail())));
    }

    @Test
    void createUserEmptyNameTest() throws Exception {
        firstUserDto.setName("  ");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(objectMapper.writeValueAsString(firstUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Параметр name не может быть пустым")));
    }

    @Test
    void updateUserTest() throws Exception {
        Mockito
                .when(userService.updateUser(anyLong(), any(UserDto.class)))
                .thenReturn(this.firstUserDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .content(objectMapper.writeValueAsString(firstUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(this.firstUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(this.firstUserDto.getName())))
                .andExpect(jsonPath("$.email", is(this.firstUserDto.getEmail())));
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isOk());
    }
}
