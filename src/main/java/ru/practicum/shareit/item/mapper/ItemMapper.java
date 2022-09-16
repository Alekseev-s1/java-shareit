package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.stream.Collectors;

@Component
public class ItemMapper {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemMapper(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    public ItemResponseDto itemToDto(Item item) {
        ItemResponseDto.ItemResponseDtoBuilder dtoBuilder = ItemResponseDto.builder();

        dtoBuilder
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(UserMapper.userToDto(item.getOwner()))
                .available(item.isAvailable());

        if (item.getRequest() != null) {
            dtoBuilder.requestId(item.getRequest().getId());
        }

        if (item.getLastBooking() != null) {
            ItemResponseDto.Booking lastBooking = new ItemResponseDto.Booking(item.getLastBooking().getId(),
                    item.getLastBooking().getBooker().getId());
            dtoBuilder.lastBooking(lastBooking);
        }

        if (item.getNextBooking() != null) {
            ItemResponseDto.Booking nextBooking = new ItemResponseDto.Booking(item.getNextBooking().getId(),
                    item.getNextBooking().getBooker().getId());
            dtoBuilder.nextBooking(nextBooking);
        }

        if (item.getComments() != null) {
            dtoBuilder.comments(
                    item.getComments().stream()
                            .map(CommentMapper::commentToDto)
                            .collect(Collectors.toList()));
        }

        return dtoBuilder.build();
    }

    public Item dtoToItem(ItemRequestDto itemDto) {
        Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());

        if (itemDto.getAvailable() == null) {
            item.setAvailable(true);
        } else {
            item.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestService.getItemRequestById(itemDto.getRequestId()));
        }

        return item;
    }
}
