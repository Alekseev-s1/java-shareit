package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponseDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam int from,
                                          @RequestParam int size) {
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestBody ItemRequestDto itemDto) {
        return new ResponseEntity<>(itemService.createItem(userId, itemDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        return itemService.updateItem(userId, itemId, itemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestParam String text,
                                             @RequestParam int from,
                                             @RequestParam int size) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long itemId,
                                         @RequestBody CommentRequestDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
