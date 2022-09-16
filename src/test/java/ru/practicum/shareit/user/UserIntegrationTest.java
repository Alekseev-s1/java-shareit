package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    void updateUserTest() {
        User user = new User();
        user.setName("Test name");
        user.setEmail("Test email");

        User userToUpdate = new User();
        userToUpdate.setName("Updated name");
        userToUpdate.setEmail("Updated email");

        userService.createUser(user);
        User updatedUser = userService.updateUser(1, userToUpdate);

        assertThat(updatedUser.getName(), equalTo(userToUpdate.getName()));
        assertThat(updatedUser.getEmail(), equalTo(userToUpdate.getEmail()));
    }
}
