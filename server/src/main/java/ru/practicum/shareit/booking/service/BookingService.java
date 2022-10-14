package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

    public BookingResponseDto getBookingById(long userId, long bookingId) {
        getUserById(userId);

        Booking booking = getBookingById(bookingId);
        Item item = booking.getItem();

        if (!(checkItemOwner(userId, item) || checkBookingOwner(userId, booking))) {
            throw new WrongOwnerException(
                    String.format("Пользователь с id = %d должен быть либо владельцем вещи с id = %d, либо автором бронирования с id = %d",
                            userId,
                            item.getId(),
                            booking.getId()));
        }

        return BookingMapper.bookingToDto(booking);
    }

    public List<BookingResponseDto> getBookings(long userId, BookingState state, int from, int size) {
        getUserById(userId);

        Pageable pagination = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository
                        .findBookingsByBooker_Id(userId, pagination);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findBookingsByBooker_IdAndStatus(userId, BookingStatus.WAITING, pagination);
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findBookingsByBooker_IdAndStatus(userId, BookingStatus.REJECTED, pagination);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findBookingsByBooker_idAndStartIsBeforeAndEndIsAfter(userId, currentDateTime, currentDateTime, pagination);
                break;
            case PAST:
                bookings = bookingRepository
                        .findBookingsByBooker_IdAndEndIsBefore(userId, currentDateTime, pagination);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findBookingsByBooker_IdAndStartIsAfter(userId, currentDateTime, pagination);
                break;
            default:
                throw new WrongBookingStateException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDto> getBookingsByItemOwner(long userId, BookingState state, int from, int size) {
        getUserById(userId);

        List<Long> userItemsId = itemRepository
                .findItemsByOwner_Id(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Pageable pagination = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Booking> bookings;

        if (userItemsId.isEmpty()) {
            return Collections.emptyList();
        }

        switch (state) {
            case ALL:
                bookings = bookingRepository
                        .findBookingsByItem_IdIn(userItemsId, pagination);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findBookingsByItem_IdInAndStatus(userItemsId, BookingStatus.WAITING, pagination);
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findBookingsByItem_IdInAndStatus(userItemsId, BookingStatus.REJECTED, pagination);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findBookingsByItem_IdInAndStartIsBeforeAndEndIsAfter(userItemsId, currentDateTime, currentDateTime, pagination);
                break;
            case PAST:
                bookings = bookingRepository
                        .findBookingsByItem_IdInAndEndIsBefore(userItemsId, currentDateTime, pagination);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findBookingsByItem_IdInAndStartIsAfter(userItemsId, currentDateTime, pagination);
                break;
            default:
                throw new WrongBookingStateException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponseDto createBooking(long userId, BookingRequestDto bookingRequestDto) {
        Booking booking = BookingMapper.dtoToBooking(bookingRequestDto);
        enrichBooking(userId, booking);

        if (!booking.getItem().isAvailable()) {
            throw new ItemUnavailableException("Данная вещь недоступна для бронирования");
        }
        if (booking.getItem().getOwner().getId() == userId) {
            throw new WrongOwnerException("Владелец вещи не может ее забронировать");
        }

        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponseDto changeStatus(long userId, long bookingId, boolean approved) {
        getUserById(userId);

        Booking booking = getBookingById(bookingId);

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

        return BookingMapper.bookingToDto(booking);
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

    private Booking getBookingById(long bookingId) {
        return bookingRepository
                .findById(bookingId)
                .orElseThrow(unitNotFoundException("Запись бронирования с id = {0} не найдена", bookingId));
    }
}
