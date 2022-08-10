package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("Пользователь с id = %d не найден", userId)));
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(long userId, User user) {
        getUserById(userId);

        return userRepository.update(userId, user);
    }

    public void deleteUser(long userId) {
        getUserById(userId);

        userRepository.delete(userId);
    }
}
