package ru.practicum.shareit.requests.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.requests.dto.ItemReqRequestDto;
import ru.practicum.shareit.requests.dto.ItemReqResponseDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest dtoToItemRequest(ItemReqRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();

        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public static ItemReqResponseDto itemRequestToDto(ItemRequest itemRequest) {
        ItemReqResponseDto itemRequestDto = new ItemReqResponseDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String created = itemRequest.getCreated().format(formatter);

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(LocalDateTime.parse(created));

        if (itemRequest.getItems() != null) {
            itemRequestDto.setItems(
                    itemRequest.getItems().stream()
                            .map(item -> new ItemReqResponseDto.Item(item.getId(),
                                    item.getName(),
                                    item.getDescription(),
                                    item.isAvailable(),
                                    item.getRequest().getId()))
                            .collect(Collectors.toList())
            );
        }

        return itemRequestDto;
    }
}
