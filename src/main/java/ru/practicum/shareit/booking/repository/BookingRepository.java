package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByBooker_Id(long userId, Sort sort);

    List<Booking> findBookingsByBooker_idAndStartIsBeforeAndEndIsAfter(long userId,
                                                                       LocalDateTime nowToStart,
                                                                       LocalDateTime nowToEnd,
                                                                       Sort sort);

    List<Booking> findBookingsByBooker_IdAndStatus(long userId, BookingStatus status, Sort sort);

    List<Booking> findBookingsByBooker_IdAndEndIsBefore(long userId, LocalDateTime now, Sort sort);

    List<Booking> findBookingsByBooker_IdAndStartIsAfter(long userId, LocalDateTime now, Sort sort);

    List<Booking> findBookingsByItem_IdInAndStartIsBeforeAndEndIsAfter(List<Long> itemsId,
                                                                       LocalDateTime nowToStart,
                                                                       LocalDateTime nowToEnd,
                                                                       Sort sort);

    List<Booking> findBookingsByItem_IdInAndStatus(List<Long> itemsId, BookingStatus status, Sort sort);

    List<Booking> findBookingsByItem_IdInAndEndIsBefore(List<Long> itemsId, LocalDateTime now, Sort sort);

    List<Booking> findBookingsByItem_IdIn(List<Long> itemsId, Sort sort);

    List<Booking> findBookingsByItem_IdInAndStartIsAfter(List<Long> itemsId, LocalDateTime now, Sort sort);

    List<Booking> findBookingsByItem_IdAndStartIsBeforeAndStatus(long itemId,
                                                                 LocalDateTime now,
                                                                 BookingStatus status,
                                                                 Sort sort);

    List<Booking> findBookingsByItem_IdAndStartIsAfterAndStatus(long itemId,
                                                                LocalDateTime now,
                                                                BookingStatus status,
                                                                Sort sort);
}
