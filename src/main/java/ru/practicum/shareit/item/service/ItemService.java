package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public List<ItemDto> getItemsByUserId(long userId) {
        return itemRepository.findItemsByUserId(userId).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(long itemId) {
        return itemRepository.findItemById(itemId)
                .map(ItemMapper::itemToDto)
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("Вещь с id = %d не найдена", itemId)));
    }

    public ItemDto createItem(long userId, ItemDto itemDto) {
        User owner = userRepository.findUserById(userId)
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("Пользователь с id = %d не найден", userId)));
        Item item = ItemMapper.dtoToItem(itemDto);
        return ItemMapper.itemToDto(itemRepository.save(owner, item));
    }

    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        User owner = userRepository.findUserById(userId)
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("Пользователь с id = %d не найден", userId)));
        ItemDto itemToUpdate = getItemById(itemId);
        checkOwner(userId, itemToUpdate);

        Item item = ItemMapper.dtoToItem(itemDto);
        return ItemMapper.itemToDto(itemRepository.updateItem(owner, itemId, item));
    }

    public void deleteItem(long itemId) {
        itemRepository.deleteItem(itemId);
    }

    public List<ItemDto> searchItem(String query) {
        if (query.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchItems(query).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    private void checkOwner(long userId, ItemDto itemDto) {
        if (itemDto.getOwner().getId() != userId) {
            throw new WrongOwnerException(
                    String.format("Пользователь с id = %d не является владельцем вещи с id = %d",
                            userId,
                            itemDto.getId()));
        }
    }
}
