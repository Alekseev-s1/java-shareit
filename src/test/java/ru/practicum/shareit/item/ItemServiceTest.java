package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.UnitNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.pageable.CustomPageable;
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

    @BeforeEach
    void setUp() {
        itemService = new ItemService(itemRepository,
                userRepository,
                bookingRepository,
                commentRepository);
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
                .findItemsByOwner_Id(anyLong(), any(CustomPageable.class));
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
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(new Item()));

        itemService.getItemById(1);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void getItemNotFoundTest() {
        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> itemService.getItemById(1));
        assertThat(exception.getMessage(), equalTo("Вещь с id = 1 не найдена"));

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void createItemTest() {
        User user = new User();

        Item item = new Item();
        item.setName("Test name");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setOwner(user);

        ArgumentCaptor<Item> itemArgumentCaptor = ArgumentCaptor.forClass(Item.class);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        itemService.createItem(1L, item);

        Mockito.verify(itemRepository, Mockito.times(1)).save(itemArgumentCaptor.capture());

        Item capturedItem = itemArgumentCaptor.getValue();

        assertThat(capturedItem.getName(), equalTo(item.getName()));
        assertThat(capturedItem.getDescription(), equalTo(item.getDescription()));
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void updateUserTest() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setOwner(user);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Item itemToUpdate = new Item();
        itemToUpdate.setName("Test name");
        itemToUpdate.setDescription("Test description");
        itemToUpdate.setAvailable(true);
        itemToUpdate.setOwner(user);

        Item updatedItem = itemService.updateItem(1, 1, itemToUpdate);

        assertThat(updatedItem.getName(), equalTo(itemToUpdate.getName()));
        assertThat(updatedItem.getDescription(), equalTo(itemToUpdate.getDescription()));
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

        Item itemToUpdate = new Item();
        itemToUpdate.setName("Test name");
        itemToUpdate.setDescription("Test description");
        itemToUpdate.setAvailable(true);
        itemToUpdate.setOwner(user);

        Exception exception = assertThrows(WrongOwnerException.class,
                () -> itemService.updateItem(2, 1, itemToUpdate));
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
                .searchItems(anyString(), any(CustomPageable.class));
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void searchItemsEmptyQueryTest() {
        List<Item> items = itemService.searchItem("", 0, 10);

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
        user.setBookings(List.of(booking));

        Comment comment = new Comment();
        comment.setText("Test comment");

        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        itemService.addComment(1, 1, comment);

        Mockito.verify(commentRepository, Mockito.times(1)).save(commentArgumentCaptor.capture());

        Comment capturedComment = commentArgumentCaptor.getValue();

        assertThat(capturedComment.getItem(), equalTo(item));
        assertThat(capturedComment.getAuthor(), equalTo(user));
        assertThat(capturedComment.getText(), equalTo(comment.getText()));
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

        Comment comment = new Comment();
        comment.setText("Test comment");

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Exception exception = assertThrows(ItemUnavailableException.class,
                () -> itemService.addComment(1, 1, comment));
        assertThat(exception.getMessage(), equalTo("Пользователь с id = 1 не может оставить комментарий к вещи с id = 1, так как еще не бронировал ее"));
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }
}
