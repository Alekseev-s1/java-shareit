package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.CustomUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserServiceImpl implements UserService {
    private final CustomUserRepository userRepository;

    @Autowired
    public CustomUserServiceImpl(CustomUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long userId) {
        return userRepository.findUserById(userId)
                .map(UserMapper::userToDto)
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("Пользователь с id = %d не найден", userId)));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .map(UserMapper::userToDto)
                .orElse(null);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        getUserById(userId);

        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.update(userId, user));
    }

    @Override
    public void deleteUser(long userId) {
        getUserById(userId);

        userRepository.delete(userId);
    }
}
