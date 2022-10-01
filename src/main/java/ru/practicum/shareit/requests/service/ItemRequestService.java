package ru.practicum.shareit.requests.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requests.dto.ItemReqRequestDto;
import ru.practicum.shareit.requests.dto.ItemReqResponseDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.UnitNotFoundException.unitNotFoundException;

@Service
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemRequestService(ItemRequestRepository itemRequestRepository,
                              UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
    }

    public ItemReqResponseDto getItemRequestById(long itemRequestId, long userId) {
        getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository
                .findById(itemRequestId)
                .orElseThrow(unitNotFoundException("Запрос с id = {0} не найден", itemRequestId));
        return ItemRequestMapper.itemRequestToDto(itemRequest);
    }

    public List<ItemReqResponseDto> getItemRequestsByUser(long userId) {
        getUserById(userId);
        return itemRequestRepository
                .findItemRequestsByRequestor_Id(userId, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(ItemRequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }

    public List<ItemReqResponseDto> getItemRequests(long userId, int from, int size) {
        getUserById(userId);
        return itemRequestRepository
                .findItemRequestsByRequestor_IdIsNot(userId, PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(ItemRequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemReqResponseDto createItemRequest(long userId, ItemReqRequestDto itemReqRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemReqRequestDto);
        User user = getUserById(userId);
        itemRequest.setRequestor(user);
        return ItemRequestMapper.itemRequestToDto(itemRequestRepository.save(itemRequest));
    }

    private User getUserById(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(unitNotFoundException("Пользователь с id = {0} не найден", userId));
    }
}
