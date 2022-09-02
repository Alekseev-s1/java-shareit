package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.UnitNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository,
                       UserRepository userRepository,
                       BookingRepository bookingRepository,
                       CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    public List<Item> getItemsByUserId(long userId) {
        return itemRepository.findItemsByOwner_Id(userId, Sort.by(Sort.Direction.ASC, "id"));
    }

    public Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new UnitNotFoundException(
                        String.format("Вещь с id = %d не найдена", itemId)));
    }

    @Transactional
    public Item createItem(long userId, Item item) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UnitNotFoundException(
                        String.format("Пользователь с id = %d не найден", userId)));
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(long userId, long itemId, Item item) {
        Item itemToUpdate = getItemById(itemId);

        if (!checkOwner(userId, itemToUpdate)) {
            throw new WrongOwnerException(
                    String.format("Пользователь с id = %d не является владельцем вещи с id = %d", userId, item.getId()));
        }

        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        itemToUpdate.setAvailable(item.isAvailable());
        /*if (item.isAvailable() != itemToUpdate.isAvailable()) {
            itemToUpdate.setAvailable(item.isAvailable());
        }*/
        if (item.getRequest() != null) {
            itemToUpdate.setRequest(item.getRequest());
        }

        return itemToUpdate;
    }

    @Transactional
    public void deleteItem(long itemId) {
        Item item = getItemById(itemId);
        itemRepository.delete(item);
    }

    public List<Item> searchItem(String query) {
        return itemRepository.searchItems(query);
    }

    @Transactional
    public Comment addComment(long itemId, long userId, Comment comment) {
        Item item = getItemById(itemId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnitNotFoundException(
                        String.format("Пользователь с id = %d не найден", userId)));

        if (user.getBookings().stream().noneMatch(booking -> booking.getItem().equals(item)
                && booking.getStatus().equals(BookingStatus.APPROVED)
                && booking.getStart().isBefore(LocalDateTime.now()))) {
            throw new ItemUnavailableException(
                    String.format("Пользователь с id = %d не может оставить комментарий к вещи с id = %d, так как еще не бронировал ее",
                            userId,
                            itemId));
        }

        comment.setItem(item);
        comment.setAuthor(user);

        return commentRepository.save(comment);
    }

    public void addBookings(Item item, long userId) {
        if (item.getBookings() != null) {

            item.setLastBooking(
                    bookingRepository.findBookingsByItem_IdAndStartIsBeforeAndStatus(item.getId(),
                                    LocalDateTime.now(),
                                    BookingStatus.APPROVED,
                                    Sort.by(Sort.Direction.DESC, "start")).stream()
                            .filter(booking -> booking.getBooker().getId() != userId)
                            .findFirst()
                            .orElse(null));

            item.setNextBooking(
                    bookingRepository.findBookingsByItem_IdAndStartIsAfterAndStatus(item.getId(),
                                    LocalDateTime.now(),
                                    BookingStatus.APPROVED,
                                    Sort.by(Sort.Direction.ASC, "start")).stream()
                            .filter(booking -> booking.getBooker().getId() != userId)
                            .findFirst()
                            .orElse(null));
        }
    }

    private boolean checkOwner(long userId, Item item) {
        return item.getOwner().getId() == userId;
    }
}
