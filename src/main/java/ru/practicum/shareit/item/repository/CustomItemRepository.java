package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CustomItemRepository {
    List<Item> findItemsByUserId(long userId);

    Optional<Item> findItemById(long itemId);

    Item save(User user, Item item);

    Item updateItem(User user, long itemId, Item item);

    void deleteItem(long itemId);

    Set<Item> searchItems(String query);
}
