package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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

        Item item = new Item();
        item.setName("Test name");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setOwner(user);

        Item itemToUpdate = new Item();
        itemToUpdate.setName("Updated name");
        itemToUpdate.setDescription("Updated description");

        User createdUser = userRepository.save(user);
        Item createdItem = itemService.createItem(createdUser.getId(), item);

        Item updatedItem = itemService.updateItem(createdUser.getId(), createdItem.getId(), itemToUpdate);

        assertThat(updatedItem.getName(), equalTo(itemToUpdate.getName()));
        assertThat(updatedItem.getDescription(), equalTo(itemToUpdate.getDescription()));
    }
}
