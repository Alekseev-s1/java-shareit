package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.exception.UnitNotFoundException.unitNotFoundException;

@Service
@Transactional(readOnly = true)
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
        return userRepository
                .findById(userId)
                .orElseThrow(unitNotFoundException("Пользователь с id = {0} не найден", userId));
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(long userId, User user) {
        User userToUpdate = getUserById(userId);
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        return userToUpdate;
    }

    @Transactional
    public void deleteUser(long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    public void checkUserExists(long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(unitNotFoundException("Пользователь с id = {0} не найден", userId));
    }
}
