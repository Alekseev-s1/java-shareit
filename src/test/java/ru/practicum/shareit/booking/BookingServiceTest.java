package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, userRepository, itemRepository);
    }

    @Test
    void getBookingByItemOwnerTest() {
        User itemOwner = new User();
        itemOwner.setId(1);

        User booker = new User();
        booker.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemOwner));

        bookingService.getBookingById(1, 1);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingByBookerTest() {
        User itemOwner = new User();
        itemOwner.setId(1);

        User booker = new User();
        booker.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));

        bookingService.getBookingById(1, 2);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingUserIsNotOwnerAndIsNotBookerTest() {
        User itemOwner = new User();
        itemOwner.setId(1);

        User booker = new User();
        booker.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(WrongOwnerException.class,
                () -> bookingService.getBookingById(1, 3));
        assertThat(exception.getMessage(), equalTo("Пользователь с id = 3 должен быть либо владельцем вещи с id = 1, либо автором бронирования с id = 1"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(3L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingUserNotFoundTest() {
        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> bookingService.getBookingById(1, 1));
        assertThat(exception.getMessage(), equalTo("Пользователь с id = 1 не найден"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void bookingNotFoundTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> bookingService.getBookingById(1, 1));
        assertThat(exception.getMessage(), equalTo("Запись бронирования с id = 1 не найдена"));

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getAllBookingsTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        bookingService.getBookings(BookingState.ALL, 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findBookingsByBooker_Id(anyLong(), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingsWrongStatusTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(WrongBookingStateException.class,
                () -> bookingService.getBookings(BookingState.TEST_STATE, 1, 0, 10));
        assertThat(exception.getMessage(), equalTo("Unknown state: TEST_STATE"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingsUserNotFoundTest() {
        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> bookingService.getBookings(BookingState.ALL, 1, 1, 1));
        assertThat(exception.getMessage(), equalTo("Пользователь с id = 1 не найден"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getAllBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner(BookingState.ALL, 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findBookingsByItem_IdIn(anyList(), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getWaitingBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner(BookingState.WAITING, 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findBookingsByItem_IdInAndStatus(anyList(), any(BookingStatus.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getRejectedBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner(BookingState.REJECTED, 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findBookingsByItem_IdInAndStatus(anyList(), any(BookingStatus.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getCurrentBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner(BookingState.CURRENT, 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findBookingsByItem_IdInAndStartIsBeforeAndEndIsAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getPastBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner(BookingState.PAST, 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findBookingsByItem_IdInAndEndIsBefore(anyList(), any(LocalDateTime.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getFutureBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner(BookingState.FUTURE, 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findBookingsByItem_IdInAndStartIsAfter(anyList(), any(LocalDateTime.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingByItemOwnerWrongStatusTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        Exception exception = assertThrows(WrongBookingStateException.class,
                () -> bookingService.getBookingsByItemOwner(BookingState.TEST_STATE, 1, 0, 10));
        assertThat(exception.getMessage(), equalTo("Unknown state: TEST_STATE"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingByWrongOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        List<Booking> items = bookingService.getBookingsByItemOwner(BookingState.ALL, 1, 0, 10);

        assertThat(items, hasSize(0));
        Mockito.verify(itemRepository, Mockito.times(1)).findItemsByOwner_Id(1);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingsByOwnerNotFoundTest() {
        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> bookingService.getBookingsByItemOwner(BookingState.ALL, 1, 1, 1));
        assertThat(exception.getMessage(), equalTo("Пользователь с id = 1 не найден"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingTest() {
        ArgumentCaptor<Booking> itemArgumentCaptor = ArgumentCaptor.forClass(Booking.class);

        User booker = new User();
        booker.setId(1);

        User itemOwner = new User();
        itemOwner.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setAvailable(true);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        booking.setItem(item);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        bookingService.createBooking(1, booking);

        Mockito.verify(bookingRepository, Mockito.times(1)).save(itemArgumentCaptor.capture());

        Booking capturedBooking = itemArgumentCaptor.getValue();

        assertThat(capturedBooking.getItem(), equalTo(item));
        assertThat(capturedBooking.getBooker(), equalTo(booker));
        assertThat(capturedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(capturedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(capturedBooking.getStatus(), equalTo(BookingStatus.WAITING));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingUserNotFoundTest() {
        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> bookingService.createBooking(1, new Booking()));
        assertThat(exception.getMessage(), equalTo("Пользователь с id = 1 не найден"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingItemNotFoundTest() {
        Item item = new Item();
        item.setId(1);

        Booking booking = new Booking();
        booking.setItem(item);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> bookingService.createBooking(1, booking));
        assertThat(exception.getMessage(), equalTo("Вещь с id = 1 не найдена"));

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingItemIsNotAvailableTest() {
        User booker = new User();
        booker.setId(1);

        User itemOwner = new User();
        itemOwner.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setItem(item);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Exception exception = assertThrows(ItemUnavailableException.class,
                () -> bookingService.createBooking(1, booking));
        assertThat(exception.getMessage(), equalTo("Данная вещь недоступна для бронирования"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingByItemOwnerTest() {
        User booker = new User();
        booker.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setOwner(booker);
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setItem(item);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Exception exception = assertThrows(WrongOwnerException.class,
                () -> bookingService.createBooking(1, booking));
        assertThat(exception.getMessage(), equalTo("Владелец вещи не может ее забронировать"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingWrongDateTest() {
        User booker = new User();
        booker.setId(1);

        User itemOwner = new User();
        itemOwner.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(2));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Exception exception = assertThrows(CrossDateException.class,
                () -> bookingService.createBooking(1, booking));
        assertThat(exception.getMessage(), equalTo("Дата окончания бронирования меньше даты начала бронирования"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void changeStatusApprovedTest() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Booking changedBooking = bookingService.changeStatus(1, 1, true);

        assertThat(changedBooking.getId(), equalTo(booking.getId()));
        assertThat(changedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void changeStatusRejectedTest() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Booking changedBooking = bookingService.changeStatus(1, 1, false);

        assertThat(changedBooking.getId(), equalTo(booking.getId()));
        assertThat(changedBooking.getStatus(), equalTo(BookingStatus.REJECTED));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void statusAlreadyApprovedTest() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Exception exception = assertThrows(StatusAlreadySetException.class,
                () -> bookingService.changeStatus(1, 1, true));
        assertThat(exception.getMessage(), equalTo("Бронирование уже было подтверждено"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void changeStatusByBookerTest() {
        User user = new User();
        user.setId(1);

        User itemOwner = new User();
        itemOwner.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Exception exception = assertThrows(WrongOwnerException.class,
                () -> bookingService.changeStatus(1, 1, true));
        assertThat(exception.getMessage(), equalTo("Пользователь с id = 1 не является владельцем вещи с id = 1"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void changeStatusBookingNotFoundTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(UnitNotFoundException.class,
                () -> bookingService.changeStatus(1, 1, true));
        assertThat(exception.getMessage(), equalTo("Запись бронирования с id = 1 не найдена"));

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }
}
