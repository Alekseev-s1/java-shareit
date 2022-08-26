package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("Пользователь с id = %d не найден", userId)));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElse(null);
    }

    @Transactional
    public User createUser(User user) {
        if (getUserByEmail(user.getEmail()) != null) {
            throw new UniqueEmailException(String.format("Пользователь с email = %s уже существует", user.getEmail()));
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(long userId, User user) {
        User userToUpdate = getUserById(userId);
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (getUserByEmail(user.getEmail()) != null && !user.getEmail().equals(userToUpdate.getEmail())) {
                throw new UniqueEmailException(String.format("Пользователь с email = %s уже существует", user.getEmail()));
            }
            userToUpdate.setEmail(user.getEmail());
        }
        return userRepository.save(userToUpdate);
    }

    @Transactional
    public void deleteUser(long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }
}
