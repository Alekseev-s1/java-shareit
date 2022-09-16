package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User itemOwner;
    private User booker;
    private Item firstItem;
    private Item secondItem;
    private Item thirdItem;
    private Booking firstBooking;
    private Booking secondBooking;
    private Booking thirdBooking;
    private Booking fourthBooking;

    @BeforeEach
    void setUp() {
        itemOwner = new User();
        itemOwner.setId(1);

        booker = new User();
        booker.setId(2);

        firstItem = new Item();
        firstItem.setId(1);
        firstItem.setAvailable(true);
        firstItem.setOwner(itemOwner);

        secondItem = new Item();
        secondItem.setId(2);
        secondItem.setAvailable(true);
        secondItem.setOwner(itemOwner);

        thirdItem = new Item();
        thirdItem.setId(3);
        thirdItem.setAvailable(true);
        thirdItem.setOwner(itemOwner);

        firstBooking = new Booking();
        firstBooking.setId(1);
        firstBooking.setItem(firstItem);
        firstBooking.setBooker(booker);
        firstBooking.setStart(LocalDateTime.now().minusDays(2));
        firstBooking.setEnd(LocalDateTime.now().plusDays(2));

        secondBooking = new Booking();
        secondBooking.setId(2);
        secondBooking.setItem(secondItem);
        secondBooking.setBooker(booker);
        secondBooking.setStart(LocalDateTime.now().minusDays(1));
        secondBooking.setEnd(LocalDateTime.now().plusDays(2));

        thirdBooking = new Booking();
        thirdBooking.setId(3);
        thirdBooking.setItem(thirdItem);
        thirdBooking.setBooker(booker);
        thirdBooking.setStart(LocalDateTime.now().minusDays(4));
        thirdBooking.setEnd(LocalDateTime.now().minusDays(2));

        fourthBooking = new Booking();
        fourthBooking.setId(4);
        fourthBooking.setItem(thirdItem);
        fourthBooking.setBooker(booker);
        fourthBooking.setStart(LocalDateTime.now().plusDays(2));
        fourthBooking.setEnd(LocalDateTime.now().plusDays(4));

        userRepository.save(itemOwner);
        userRepository.save(booker);
        itemRepository.save(firstItem);
        itemRepository.save(secondItem);
        itemRepository.save(thirdItem);
        bookingService.createBooking(booker.getId(), firstBooking);
        bookingService.createBooking(booker.getId(), secondBooking);
        bookingService.createBooking(booker.getId(), thirdBooking);
        bookingService.createBooking(booker.getId(), fourthBooking);
        bookingService.changeStatus(firstBooking.getId(), itemOwner.getId(), false);
    }

    @Test
    void getAllBookingsTest() {
        List<Booking> bookings = bookingService.getBookings(BookingState.ALL, booker.getId(), 0, 10);

        assertThat(bookings, hasSize(4));
        assertThat(bookings.get(0), equalTo(fourthBooking));
        assertThat(bookings.get(1), equalTo(secondBooking));
        assertThat(bookings.get(2), equalTo(firstBooking));
        assertThat(bookings.get(3), equalTo(thirdBooking));
    }

    @Test
    void getRejectedBookingsTest() {
        List<Booking> bookings = bookingService.getBookings(BookingState.REJECTED, booker.getId(), 0, 10);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0), equalTo(firstBooking));
    }

    @Test
    void getWaitingBookingsTest() {
        List<Booking> bookings = bookingService.getBookings(BookingState.WAITING, booker.getId(), 0, 10);

        assertThat(bookings, hasSize(3));
        assertThat(bookings.get(0), equalTo(fourthBooking));
        assertThat(bookings.get(1), equalTo(secondBooking));
        assertThat(bookings.get(2), equalTo(thirdBooking));
    }

    @Test
    void getCurrentBookingsTest() {
        List<Booking> bookings = bookingService.getBookings(BookingState.CURRENT, booker.getId(), 0, 10);

        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0), equalTo(secondBooking));
        assertThat(bookings.get(1), equalTo(firstBooking));
    }

    @Test
    void getPastBookingsTest() {
        List<Booking> bookings = bookingService.getBookings(BookingState.PAST, booker.getId(), 0, 10);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0), equalTo(thirdBooking));
    }

    @Test
    void getFutureBookingsTest() {
        List<Booking> bookings = bookingService.getBookings(BookingState.FUTURE, booker.getId(), 0, 10);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0), equalTo(fourthBooking));
    }
}
