package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Setter
@Getter
public class Booking {
    private long id;
    private Item item;
    private LocalDate start;
    private LocalDate end;
    private User booker;
    private BookingStatus status;
}
