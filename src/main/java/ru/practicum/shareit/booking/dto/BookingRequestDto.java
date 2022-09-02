package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
public class BookingRequestDto {
    private long id;

    @NotNull(message = "Необходимо указать itemId для бронирования")
    private Long itemId;

    @NotNull(message = "Необходимо указать дату начала бронирования (start)")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Необходимо указать дату окончания бронирования (end)")
    @Future(message = "Дата окончания бронирования не может быть в прошлом")
    private LocalDateTime end;
}
