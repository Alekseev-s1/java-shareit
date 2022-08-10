package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> findUserById(long userId);

    User save(User user);

    User update(long userId, User user);

    void delete(long userId);
}
