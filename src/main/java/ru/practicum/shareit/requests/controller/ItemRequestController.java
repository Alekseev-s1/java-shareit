package ru.practicum.shareit.requests.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemReqRequestDto;
import ru.practicum.shareit.requests.dto.ItemReqResponseDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService,
                                 UserService userService) {
        this.itemRequestService = itemRequestService;
        this.userService = userService;
    }

    @GetMapping("/{requestId}")
    public ItemReqResponseDto getItemRequestById(@PathVariable long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        userService.checkUserExists(userId);
        return ItemRequestMapper.itemRequestToDto(itemRequestService.getItemRequestById(requestId));
    }

    @GetMapping
    public List<ItemReqResponseDto> getItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        userService.checkUserExists(userId);
        return itemRequestService.getItemRequestsByUser(userId).stream()
                .map(ItemRequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemReqResponseDto> getItemRequests(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @Positive int size,
                                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        userService.checkUserExists(userId);
        return itemRequestService.getItemRequests(userId, from, size).stream()
                .map(ItemRequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemReqResponseDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid ItemReqRequestDto itemRequestDto) {
        userService.checkUserExists(userId);
        return ItemRequestMapper.itemRequestToDto(
                itemRequestService.createItem(userId, ItemRequestMapper.dtoToItemRequest(itemRequestDto)));
    }
}
