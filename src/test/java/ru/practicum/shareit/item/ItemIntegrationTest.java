package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemIntegrationTest {
    @Autowired
    ItemService itemService;

    @Autowired
    UserRepository userRepository;

    @Test
    void updateItem() {
        User user = new User();
        user.setName("Test name");
        user.setEmail("Test email");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("Test name");
        itemRequestDto.setDescription("Test description");
        itemRequestDto.setAvailable(true);
        itemRequestDto.setRequestId(1L);

        ItemRequestDto itemToUpdateDto = new ItemRequestDto();
        itemToUpdateDto.setName("Updated name");
        itemToUpdateDto.setDescription("Updated description");
        itemToUpdateDto.setAvailable(true);

        User createdUser = userRepository.save(user);
        ItemResponseDto createdItemDto = itemService.createItem(createdUser.getId(), itemRequestDto);

        ItemResponseDto updatedItemDto = itemService.updateItem(createdUser.getId(), createdItemDto.getId(), itemToUpdateDto);

        assertThat(updatedItemDto.getName(), equalTo(itemToUpdateDto.getName()));
        assertThat(updatedItemDto.getDescription(), equalTo(itemToUpdateDto.getDescription()));
    }
}
