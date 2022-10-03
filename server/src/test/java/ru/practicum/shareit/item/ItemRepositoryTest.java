package ru.practicum.shareit.item;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void searchItemByName() {
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");

        userRepository.save(user);

        Item firstItem = new Item();
        firstItem.setName("First name");
        firstItem.setDescription("Description");
        firstItem.setAvailable(true);
        firstItem.setOwner(user);
        firstItem.setRequest(null);

        Item secondItem = new Item();
        secondItem.setName("Second name");
        secondItem.setDescription("Description");
        secondItem.setAvailable(true);
        secondItem.setOwner(user);
        secondItem.setRequest(null);

        itemRepository.save(firstItem);
        itemRepository.save(secondItem);

        List<Item> foundItems = itemRepository.searchItems("first", null);
        assertThat(foundItems, Matchers.hasSize(1));

        Item foundItem = foundItems.get(0);
        assertThat(foundItem.getName(), equalTo(firstItem.getName()));
        assertThat(foundItem.getDescription(), equalTo(firstItem.getDescription()));
    }

    @Test
    void searchItemByDescription() {
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");

        userRepository.save(user);

        Item firstItem = new Item();
        firstItem.setName("Name");
        firstItem.setDescription("First description");
        firstItem.setAvailable(true);
        firstItem.setOwner(user);
        firstItem.setRequest(null);

        Item secondItem = new Item();
        secondItem.setName("Name");
        secondItem.setDescription("Second description");
        secondItem.setAvailable(true);
        secondItem.setOwner(user);
        secondItem.setRequest(null);

        itemRepository.save(firstItem);
        itemRepository.save(secondItem);

        List<Item> foundItems = itemRepository.searchItems("second", null);
        assertThat(foundItems, Matchers.hasSize(1));

        Item foundItem = foundItems.get(0);
        assertThat(foundItem.getName(), equalTo(secondItem.getName()));
        assertThat(foundItem.getDescription(), equalTo(secondItem.getDescription()));
    }
}
