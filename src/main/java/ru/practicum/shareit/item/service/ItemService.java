package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsByUserId(long userId) {
        User owner = getUserById(userId);
        return itemRepository.findItemsByOwner(owner);
    }

    @Transactional(readOnly = true)
    public Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("Вещь с id = %d не найдена", itemId)));
    }

    @Transactional
    public Item createItem(long userId, Item item) {
        User owner = getUserById(userId);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(long userId, long itemId, Item item) {
        Item itemToUpdate = getItemById(itemId);
        checkOwner(userId, itemToUpdate);

        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        if (item.getRequest() != null) {
            itemToUpdate.setRequest(item.getRequest());
        }

        return itemRepository.save(itemToUpdate);
    }

    @Transactional
    public void deleteItem(long itemId) {
        Item item = getItemById(itemId);
        itemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public List<Item> searchItem(String query) {
        return itemRepository.findItemsByNameContainingOrDescriptionContaining(query);
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("Пользователь с id = %d не найден", userId)));
    }

    private void checkOwner(long userId, Item item) {
        if (item.getOwner().getId() != userId) {
            throw new WrongOwnerException(
                    String.format("Пользователь с id = %d не является владельцем вещи с id = %d", userId, item.getId()));
        }
    }
}
