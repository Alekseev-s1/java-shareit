package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long userId) {
        return userRepository.findUserById(userId)
                .map(UserMapper::userToDto)
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("Пользователь с id = %d не найден", userId)));
    }

    public UserDto createUser(UserDto userDto) {
        checkIfEmailExists(userDto);
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.save(user));
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        getUserById(userId);
        checkIfEmailExists(userId, userDto);

        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.update(userId, user));
    }

    public void deleteUser(long userId) {
        getUserById(userId);

        userRepository.delete(userId);
    }

    private void checkIfEmailExists(UserDto userDto) {
        if (userRepository.findUserByEmail(userDto.getEmail()).isPresent()) {
            throw new UniqueEmailException("Пользователь с таким email уже существует");
        }
    }

    private void checkIfEmailExists(long userId, UserDto userDto) {
        Optional<User> userOptional = userRepository.findUserByEmail(userDto.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getId() != userId) {
                throw new UniqueEmailException("Пользователь с таким email уже существует");
            }
        }
    }
}
