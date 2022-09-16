package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UnitNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void getUsersTest() {
        userService.getUsers();

        Mockito.verify(userRepository).findAll();
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserByIdTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        userService.getUserById(1);

        Mockito.verify(userRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getNotFoundUserTest() {
        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> userService.getUserById(1));
        assertThat(exception.getMessage(), equalTo("Пользователь с id = 1 не найден"));

        Mockito.verify(userRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUserTest() {
        User user = new User();
        user.setName("Test user");
        user.setEmail("test@test.com");

        userService.createUser(user);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        Mockito.verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getName(), equalTo(user.getName()));
        assertThat(capturedUser.getEmail(), equalTo(user.getEmail()));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        User user = new User();
        user.setName("Test user");
        user.setEmail("test@test.com");

        User updatedUser = userService.updateUser(1, user);

        assertThat(user.getName(), equalTo(updatedUser.getName()));
        assertThat(user.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    void deleteUserTest() {
        User user = new User();

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1);

        Mockito.verify(userRepository).delete(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}
