package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.UnitNotFoundException;
import ru.practicum.shareit.pageable.CustomPageable;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    private ItemRequestService itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestService(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestByIdTest() {
        Mockito
                .when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(new ItemRequest()));

        itemRequestService.getItemRequestById(1);

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestByIdNotFoundTest() {
        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> itemRequestService.getItemRequestById(1));
        assertThat(exception.getMessage(), equalTo("Запрос с id = 1 не найден"));

        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestsByUserIdTest() {
        Mockito
                .when(itemRequestRepository.findItemRequestsByRequestor_Id(anyLong(), any(Sort.class)))
                .thenReturn(List.of(new ItemRequest(), new ItemRequest()));

        itemRequestService.getItemRequestsByUser(1);

        Mockito
                .verify(itemRequestRepository, Mockito.times(1))
                .findItemRequestsByRequestor_Id(anyLong(), any(Sort.class));
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestsByUserIdNotFoundTest() {
        List<ItemRequest> itemRequests = itemRequestService.getItemRequestsByUser(1);

        assertThat(itemRequests, hasSize(0));
        Mockito
                .verify(itemRequestRepository, Mockito.times(1))
                .findItemRequestsByRequestor_Id(anyLong(), any(Sort.class));
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestsTest() {
        Mockito
                .when(itemRequestRepository.findItemRequestsByRequestor_IdIsNot(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(new ItemRequest(), new ItemRequest()));

        itemRequestService.getItemRequests(1, 0, 10);

        Mockito
                .verify(itemRequestRepository, Mockito.times(1))
                .findItemRequestsByRequestor_IdIsNot(anyLong(), any(CustomPageable.class));
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestsNotFoundTest() {
        List<ItemRequest> itemRequests = itemRequestService.getItemRequests(1, 0, 10);

        assertThat(itemRequests, hasSize(0));
        Mockito
                .verify(itemRequestRepository, Mockito.times(1))
                .findItemRequestsByRequestor_IdIsNot(anyLong(), any(CustomPageable.class));
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void createItemTest() {
        User requestor = new User();
        requestor.setId(1);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Test description");

        ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor = ArgumentCaptor.forClass(ItemRequest.class);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));

        itemRequestService.createItemRequest(1, itemRequest);

        Mockito
                .verify(itemRequestRepository, Mockito.times(1))
                .save(itemRequestArgumentCaptor.capture());

        ItemRequest capturedItemRequest = itemRequestArgumentCaptor.getValue();

        assertThat(capturedItemRequest.getRequestor(), equalTo(requestor));
        assertThat(capturedItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }
}