package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemResponseDto itemToDto(Item item) {
        ItemResponseDto itemDto = new ItemResponseDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setOwner(UserMapper.userToDto(item.getOwner()));
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(BookingMapper.bookingToItemBookingDto(item.getLastBooking()));
        itemDto.setNextBooking(BookingMapper.bookingToItemBookingDto(item.getNextBooking()));
        if (item.getComments() != null) {
            itemDto.setComments(item.getComments().stream()
                    .map(CommentMapper::commentToDto)
                    .collect(Collectors.toList()));
        }
        return itemDto;
    }

    public static Item dtoToItem(ItemRequestDto itemDto) {
        Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
