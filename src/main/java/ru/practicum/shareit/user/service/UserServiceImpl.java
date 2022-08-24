package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
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
        return userRepository.findById(userId)
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
    public UserDto updateUser(long id, UserDto userDto) {
        User userToUpdate = UserMapper.dtoToUser(getUserById(id));
        if (userDto.getEmail() != null) {
            userToUpdate.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }
        return UserMapper.userToDto(userRepository.save(userToUpdate));
    }

    @Override
    public void deleteUser(long userId) {
        User user = UserMapper.dtoToUser(getUserById(userId));

        userRepository.delete(user);
    }
}
