package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private static final Map<Long, Item> items = new HashMap<>();
    private static final Map<Long, List<Long>> itemsByUser = new HashMap<>();
    private static long id = 1;

    @Override
    public List<Item> findItemsByUserId(long userId) {
        List<Long> itemsId = itemsByUser.get(userId);
        return itemsId.stream()
                .map(items::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findItemById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item save(User user, Item item) {
        item.setOwner(user);
        item.setId(id++);
        items.put(item.getId(), item);
        itemsByUser.compute(user.getId(), (userId, userItemsId) -> {
            if (userItemsId == null) {
                userItemsId = new ArrayList<>();
            }
            userItemsId.add(item.getId());
            return userItemsId;
        });
        return item;
    }

    @Override
    public Item updateItem(User user, long itemId, Item item) {
        Item itemToUpdate = items.get(itemId);

        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }

        return itemToUpdate;
    }

    @Override
    public void deleteItem(long itemId) {
        User owner = items.get(itemId).getOwner();
        itemsByUser.compute(owner.getId(), (userId, userItemsId) -> {
            userItemsId.remove(itemId);
            return userItemsId;
        });
        items.remove(itemId);
    }

    @Override
    public Set<Item> searchItems(String query) {
        List<Item> foundByName = items.values().stream()
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        List<Item> foundByDescription = items.values().stream()
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> item.getDescription().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        Set<Item> items = new TreeSet<>(Comparator.comparingLong(Item::getId));
        items.addAll(foundByName);
        items.addAll(foundByDescription);
        return items;
    }
}
