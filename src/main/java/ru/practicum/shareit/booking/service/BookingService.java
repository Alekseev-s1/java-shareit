package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.CustomPageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.UnitNotFoundException.unitNotFoundException;

@Service
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public Booking getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(unitNotFoundException("Запись бронирования с id = {0} не найдена", bookingId));
        Item item = booking.getItem();

        if (!(checkItemOwner(userId, item) || checkBookingOwner(userId, booking))) {
            throw new WrongOwnerException(
                    String.format("Пользователь с id = %d должен быть либо владельцем вещи с id = %d, либо автором бронирования с id = %d",
                            userId,
                            item.getId(),
                            booking.getId()));
        }

        return booking;
    }

    public List<Booking> getBookings(BookingState state, long userId, int from, int size) {
        getUserById(userId);
        Pageable pagination = CustomPageable.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                return bookingRepository
                        .findBookingsByBooker_Id(userId, pagination);
            case WAITING:
                return bookingRepository
                        .findBookingsByBooker_IdAndStatus(userId, BookingStatus.WAITING, pagination);
            case REJECTED:
                return bookingRepository
                        .findBookingsByBooker_IdAndStatus(userId, BookingStatus.REJECTED, pagination);
            case CURRENT:
                return bookingRepository
                        .findBookingsByBooker_idAndStartIsBeforeAndEndIsAfter(userId, now, now, pagination);
            case PAST:
                return bookingRepository
                        .findBookingsByBooker_IdAndEndIsBefore(userId, now, pagination);
            case FUTURE:
                return bookingRepository
                        .findBookingsByBooker_IdAndStartIsAfter(userId, now, pagination);
            default:
                throw new WrongBookingStateException("Unknown state: " + state);
        }
    }

    public List<Booking> getBookingsByItemOwner(BookingState state, long userId, int from, int size) {
        getUserById(userId);
        List<Long> userItemsId = itemRepository
                .findItemsByOwner_Id(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Pageable pagination = CustomPageable.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime now = LocalDateTime.now();

        if (userItemsId.isEmpty()) {
            return Collections.emptyList();
        }

        switch (state) {
            case ALL:
                return bookingRepository
                        .findBookingsByItem_IdIn(userItemsId, pagination);
            case WAITING:
                return bookingRepository
                        .findBookingsByItem_IdInAndStatus(userItemsId, BookingStatus.WAITING, pagination);
            case REJECTED:
                return bookingRepository
                        .findBookingsByItem_IdInAndStatus(userItemsId, BookingStatus.REJECTED, pagination);
            case CURRENT:
                return bookingRepository
                        .findBookingsByItem_IdInAndStartIsBeforeAndEndIsAfter(userItemsId, now, now, pagination);
            case PAST:
                return bookingRepository
                        .findBookingsByItem_IdInAndEndIsBefore(userItemsId, now, pagination);
            case FUTURE:
                return bookingRepository
                        .findBookingsByItem_IdInAndStartIsAfter(userItemsId, now, pagination);
            default:
                throw new WrongBookingStateException("Unknown state: " + state);
        }
    }

    @Transactional
    public Booking createBooking(long userId, Booking booking) {
        enrichBooking(userId, booking);

        if (!booking.getItem().isAvailable()) {
            throw new ItemUnavailableException("Данная вещь недоступна для бронирования");
        }
        if (booking.getItem().getOwner().getId() == userId) {
            throw new WrongOwnerException("Владелец вещи не может ее забронировать");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new CrossDateException("Дата окончания бронирования меньше даты начала бронирования");
        }

        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking changeStatus(long bookingId, long userId, boolean approved) {
        Booking booking = getBookingById(bookingId, userId);

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new StatusAlreadySetException("Бронирование уже было подтверждено");
        }

        Item item = booking.getItem();

        if (!checkItemOwner(userId, item)) {
            throw new WrongOwnerException(
                    String.format("Пользователь с id = %d не является владельцем вещи с id = %d", userId, item.getId()));
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return booking;
    }

    private boolean checkItemOwner(long userId, Item item) {
        return item.getOwner().getId() == userId;
    }

    private boolean checkBookingOwner(long userId, Booking booking) {
        return booking.getBooker().getId() == userId;
    }

    private void enrichBooking(long userId, Booking booking) {
        User booker = getUserById(userId);
        Item item = itemRepository
                .findById(booking.getItem().getId())
                .orElseThrow(unitNotFoundException("Вещь с id = {0} не найдена", booking.getItem().getId()));

        booking.setBooker(booker);
        booking.setItem(item);
    }

    private User getUserById(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(unitNotFoundException("Пользователь с id = {0} не найден", userId));
    }
}
