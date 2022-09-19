package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.UnitNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {
        itemService = new ItemService(itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
    }

    @Test
    void getItemsByUserIdTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        itemService.getItemsByUserId(1, 0, 10);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito
                .verify(itemRepository, Mockito.times(1))
                .findItemsByOwner_Id(anyLong(), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void getItemByUserNotFoundTest() {
        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> itemService.getItemsByUserId(1, 0, 10));
        assertThat(exception.getMessage(), equalTo("Пользователь с id = 1 не найден"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void getItemByIdTest() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setOwner(user);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        itemService.getItem(1, 1);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void getItemNotFoundTest() {
        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> itemService.getItem(1, 1));
        assertThat(exception.getMessage(), equalTo("Вещь с id = 1 не найдена"));

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void createItemTest() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setOwner(user);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("Test name");
        itemRequestDto.setDescription("Test description");
        itemRequestDto.setAvailable(true);

        ArgumentCaptor<Item> itemArgumentCaptor = ArgumentCaptor.forClass(Item.class);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        itemService.createItem(1L, itemRequestDto);

        Mockito.verify(itemRepository, Mockito.times(1)).save(itemArgumentCaptor.capture());

        Item capturedItem = itemArgumentCaptor.getValue();

        assertThat(capturedItem.getName(), equalTo(itemRequestDto.getName()));
        assertThat(capturedItem.getDescription(), equalTo(itemRequestDto.getDescription()));
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void updateItemTest() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setOwner(user);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        ItemRequestDto itemToUpdateDto = new ItemRequestDto();
        itemToUpdateDto.setName("Test name");
        itemToUpdateDto.setDescription("Test description");
        itemToUpdateDto.setAvailable(true);

        ItemResponseDto updatedItemDto = itemService.updateItem(1, 1, itemToUpdateDto);

        assertThat(updatedItemDto.getName(), equalTo(itemToUpdateDto.getName()));
        assertThat(updatedItemDto.getDescription(), equalTo(itemToUpdateDto.getDescription()));
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void updateItemByWrongUser() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setOwner(user);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        ItemRequestDto itemToUpdateDto = new ItemRequestDto();
        itemToUpdateDto.setName("Test name");
        itemToUpdateDto.setDescription("Test description");
        itemToUpdateDto.setAvailable(true);

        Exception exception = assertThrows(WrongOwnerException.class,
                () -> itemService.updateItem(2, 1, itemToUpdateDto));
        assertThat(exception.getMessage(), equalTo("Пользователь с id = 2 не является владельцем вещи с id = 1"));
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void deleteItemTest() {
        Item item = new Item();

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        itemService.deleteItem(1);

        Mockito.verify(itemRepository, Mockito.times(1)).delete(item);
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void searchItemsTest() {
        itemService.searchItem("Test", 0, 10);

        Mockito
                .verify(itemRepository, Mockito.times(1))
                .searchItems(anyString(), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void searchItemsEmptyQueryTest() {
        List<ItemResponseDto> items = itemService.searchItem("", 0, 10);

        assertThat(items, hasSize(0));
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void addCommentTest() {
        Item item = new Item();
        item.setId(1);

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(1));

        User user = new User();
        user.setId(1);
        user.setName("User name");
        user.setBookings(List.of(booking));

        Comment comment = new Comment();
        comment.setId(1);
        comment.setItem(item);
        comment.setAuthor(user);

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Test comment");

        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        itemService.addComment(1, 1, commentRequestDto);

        Mockito.verify(commentRepository, Mockito.times(1)).save(commentArgumentCaptor.capture());

        Comment capturedComment = commentArgumentCaptor.getValue();

        assertThat(capturedComment.getItem(), equalTo(item));
        assertThat(capturedComment.getAuthor(), equalTo(user));
        assertThat(capturedComment.getText(), equalTo(commentRequestDto.getText()));
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void addCommentByWrongUser() {
        Item item = new Item();
        item.setId(1);

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(1));

        User user = new User();
        user.setId(1);
        user.setBookings(Collections.emptyList());

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Test comment");

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Exception exception = assertThrows(ItemUnavailableException.class,
                () -> itemService.addComment(1, 1, commentRequestDto));
        assertThat(exception.getMessage(), equalTo("Пользователь с id = 1 не может оставить комментарий к вещи с id = 1, так как еще не бронировал ее"));
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }
}
