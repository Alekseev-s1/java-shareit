package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
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

    private User firstUser;

    @BeforeEach
    void setUp() {
        firstUser = new User();
        firstUser.setId(1);
        firstUser.setName("First name");
        firstUser.setEmail("first@test.com");
    }

    @Test
    void getUsersTest() throws Exception {
        User secondUser = new User();
        secondUser.setId(2);
        secondUser.setName("Second name");
        secondUser.setEmail("second@test.com");

        Mockito
                .when(userService.getUsers())
                .thenReturn(List.of(firstUser, secondUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(firstUser.getName())))
                .andExpect(jsonPath("$[0].email", is(firstUser.getEmail())))
                .andExpect(jsonPath("$[1].id", is(secondUser.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(secondUser.getName())))
                .andExpect(jsonPath("$[1].email", is(secondUser.getEmail())));
    }

    @Test
    void getUserByIdTest() throws Exception {
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(firstUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName())))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail())));
    }

    @Test
    void createUserTest() throws Exception {
        UserDto firstUserDto = new UserDto();
        firstUserDto.setName("First name");
        firstUserDto.setEmail("first@test.com");

        Mockito
                .when(userService.createUser(any(User.class)))
                .thenReturn(firstUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(objectMapper.writeValueAsString(firstUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName())))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail())));
    }

    @Test
    void createUserEmptyNameTest() throws Exception {
        UserDto firstUserDto = new UserDto();
        firstUserDto.setName("  ");
        firstUserDto.setEmail("first@test.com");

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
        UserDto firstUserDto = new UserDto();
        firstUserDto.setName("First name");
        firstUserDto.setEmail("first@test.com");

        Mockito
                .when(userService.updateUser(anyLong(), any(User.class)))
                .thenReturn(firstUser);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .content(objectMapper.writeValueAsString(firstUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName())))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail())));
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isOk());
    }
}
