package ru.practicum.shareit.requests.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

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

    public ItemRequest getItemRequestById(long itemRequestId) {
        return itemRequestRepository
                .findById(itemRequestId)
                .orElseThrow(unitNotFoundException("Запрос с id = {0} не найден", itemRequestId));
    }

    public List<ItemRequest> getItemRequestsByUser(long userId) {
        return itemRequestRepository
                .findItemRequestsByRequestor_Id(userId, Sort.by(Sort.Direction.DESC, "created"));
    }

    public List<ItemRequest> getItemRequests(long userId, int from, int size) {
        return itemRequestRepository.findItemRequestsByRequestor_IdIsNot(userId,
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")));
    }

    @Transactional
    public ItemRequest createItemRequest(long userId, ItemRequest itemRequest) {
        User user = getUserById(userId);
        itemRequest.setRequestor(user);
        return itemRequestRepository.save(itemRequest);
    }

    private User getUserById(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(unitNotFoundException("Пользователь с id = {0} не найден", userId));
    }
}
