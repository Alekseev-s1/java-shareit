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
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.CrossDateException;
import ru.practicum.shareit.exception.StatusAlreadySetException;
import ru.practicum.shareit.exception.WrongOwnerException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private BookingResponseDto bookingResponseDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        BookingResponseDto.Booker booker = new BookingResponseDto.Booker(1, "Booker name");
        BookingResponseDto.Item item = new BookingResponseDto.Item(1, "Item name");

        bookingResponseDto = BookingResponseDto.builder()
                .id(1)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();

    }

    @Test
    void getBookingTest() throws Exception {
        Mockito
                .when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name())));
    }

    @Test
    void getBookingsTest() throws Exception {
        Mockito
                .when(bookingService.getBookings(any(BookingState.class), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponseDto.getItem().getName())))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.APPROVED.name())));
    }

    @Test
    void getBookingsByOwnerTest() throws Exception {
        Mockito
                .when(bookingService.getBookingsByItemOwner(any(BookingState.class), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponseDto.getItem().getName())))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.APPROVED.name())));
    }

    @Test
    void createBookingTest() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(2));
        requestDto.setEnd(LocalDateTime.now().plusDays(4));

        Mockito
                .when(bookingService.createBooking(anyLong(), any(BookingRequestDto.class)))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name())));
    }

    @Test
    void createBookingInvalidDateTest() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(2));
        requestDto.setEnd(LocalDateTime.now().plusDays(4));

        Mockito
                .when(bookingService.createBooking(anyLong(), any(BookingRequestDto.class)))
                .thenThrow(new CrossDateException("Дата окончания бронирования меньше даты начала бронирования"));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Дата окончания бронирования меньше даты начала бронирования")));
    }

    @Test
    void changeStatusTest() throws Exception {
        Mockito
                .when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name())));
    }

    @Test
    void bookingAlreadyApprovedTest() throws Exception {
        Mockito
                .when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new StatusAlreadySetException("Бронирование уже было подтверждено"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Бронирование уже было подтверждено")));
    }

    @Test
    void getBookingByWrongOwnerTest() throws Exception {
        Mockito
                .when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new WrongOwnerException("Пользователь с id = 1 должен быть либо владельцем вещи с id = 1, либо автором бронирования с id = 1"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Пользователь с id = 1 должен быть либо владельцем вещи с id = 1, либо автором бронирования с id = 1")));
    }

    @Test
    void getBookingsZeroSizeTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("must be greater than 0")));
    }

    @Test
    void getBookingsWrongStateTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "OLOLO"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Unknown state: OLOLO")));
    }
}
