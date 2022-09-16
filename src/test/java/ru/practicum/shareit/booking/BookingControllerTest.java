package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1);
        booker.setName("Test booker name");

        item = new Item();
        item.setId(1);
        item.setName("Test item name");

        booking = new Booking();
        booking.setId(1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
    }

    @Test
    void getBookingTest() throws Exception {
        Mockito
                .when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(booking.getBooker().getName())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name())));
    }

    @Test
    void getBookingsTest() throws Exception {
        Mockito
                .when(bookingService.getBookings(any(BookingState.class), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.APPROVED.name())));
    }

    @Test
    void getBookingsByOwnerTest() throws Exception {
        Mockito
                .when(bookingService.getBookingsByItemOwner(any(BookingState.class), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(booking.getBooker().getName())))
                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.APPROVED.name())));
    }

    @Test
    void createBookingTest() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(2));
        requestDto.setEnd(LocalDateTime.now().plusDays(4));

        Mockito
                .when(bookingService.createBooking(anyLong(), any(Booking.class)))
                .thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(booking.getBooker().getName())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name())));
    }

    @Test
    void changeStatusTest() throws Exception {
        Mockito
                .when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(booking.getBooker().getName())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name())));
    }
}
