package ru.practicum.shareit.requests.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemReqResponseDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<Item> items;

    @Data
    public static class Item {
        private final long id;
        private final String name;
        private final String description;
        private final boolean available;
        private final long requestId;
    }
}
