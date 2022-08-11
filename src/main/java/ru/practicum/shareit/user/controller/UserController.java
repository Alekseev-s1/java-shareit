package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.UniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        checkIfEmailExists(userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,
                              @RequestBody @Valid UserDto userDto) {
        checkIfEmailExists(userId, userDto);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }

    private void checkIfEmailExists(UserDto userDto) {
        UserDto user = userService.getUserByEmail(userDto.getEmail());
        if (user != null) {
            throw new UniqueEmailException("Пользователь с таким email уже существует");
        }
    }

    private void checkIfEmailExists(long userId, UserDto userDto) {
        UserDto user = userService.getUserByEmail(userDto.getEmail());
        if (user != null) {
            if (user.getId() != userId) {
                throw new UniqueEmailException("Пользователь с таким email уже существует");
            }
        }
    }
}
