package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
        Item item = ItemMapper.dtoToItem(itemDto);
        return ItemMapper.itemToDto(itemRepository.updateItem(owner, itemId, item));
    }

    public List<ItemDto> searchItem(String query) {
        return itemRepository.searchItems(query).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }
}
