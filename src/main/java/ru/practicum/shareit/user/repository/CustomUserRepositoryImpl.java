package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class CustomUserRepositoryImpl implements CustomUserRepository {
    private static final Map<Long, User> users = new HashMap<>();
    private final Set<String> uniqueEmails = new HashSet<>();
    private static long id = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User save(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        uniqueEmails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(long userId, User user) {
        User userToUpdate = users.get(userId);

        if (user.getEmail() != null) {
            uniqueEmails.remove(userToUpdate.getEmail());
            uniqueEmails.add(user.getEmail());
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }

        return userToUpdate;
    }

    @Override
    public void delete(long userId) {
        uniqueEmails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }
}
