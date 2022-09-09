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
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemService itemService,
                          ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @GetMapping
    public List<ItemResponseDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        return itemService.getItemsByUserId(userId, from, size).stream()
                .peek(item -> itemService.addBookings(item, userId))
                .map(itemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@PathVariable long itemId,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        Item item = itemService.getItemById(itemId);
        itemService.addBookings(item, userId);
        return itemMapper.itemToDto(item);
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestBody @Validated(OnCreate.class) ItemRequestDto itemDto) {
        return new ResponseEntity<>(itemMapper.itemToDto(itemService.createItem(userId, itemMapper.dtoToItem(itemDto))),
                HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId,
                                      @RequestBody @Validated(OnUpdate.class) ItemRequestDto itemDto) {
        return itemMapper.itemToDto(itemService.updateItem(userId,
                itemId,
                itemMapper.dtoToItem(itemDto)));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestParam String text,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        return itemService.searchItem(text, from, size).stream()
                .map(itemMapper::itemToDto)
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
