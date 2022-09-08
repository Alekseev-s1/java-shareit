package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponseDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsByUserId(userId).stream()
                .peek(item -> itemService.addBookings(item, userId))
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@PathVariable long itemId,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        Item item = itemService.getItemById(itemId);
        itemService.addBookings(item, userId);
        return ItemMapper.itemToDto(item);
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestBody @Validated(OnCreate.class) ItemRequestDto itemDto) {
        return new ResponseEntity<>(ItemMapper.itemToDto(itemService.createItem(userId, ItemMapper.dtoToItem(itemDto))),
                HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId,
                                      @RequestBody @Validated(OnUpdate.class) ItemRequestDto itemDto) {
        return ItemMapper.itemToDto(itemService.updateItem(userId,
                itemId,
                ItemMapper.dtoToItem(itemDto)));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestParam String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.searchItem(text).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @PostMapping("{itemId}/comment")
    public CommentResponseDto addComment(@PathVariable long itemId,
                                         @RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid CommentRequestDto commentDto) {
        return CommentMapper.commentToDto(itemService.addComment(itemId,
                userId,
                CommentMapper.dtoToComment(commentDto)));
    }
}
