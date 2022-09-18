package ru.practicum.shareit.requests.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemReqRequestDto;
import ru.practicum.shareit.requests.dto.ItemReqResponseDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping("/{requestId}")
    public ItemReqResponseDto getItemRequestById(@PathVariable long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }

    @GetMapping
    public List<ItemReqResponseDto> getItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemReqResponseDto> getItemRequests(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @Positive int size,
                                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getItemRequests(userId, from, size);
    }

    @PostMapping
    public ItemReqResponseDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid ItemReqRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }
}
