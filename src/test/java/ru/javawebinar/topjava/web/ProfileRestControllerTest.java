package ru.javawebinar.topjava.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.to.RestaurantTO;
import ru.javawebinar.topjava.util.RestaurantUtils;
import ru.javawebinar.topjava.util.TestUtils;
import ru.javawebinar.topjava.util.exception.ErrorType;
import ru.javawebinar.topjava.util.json.JsonUtils;
import ru.javawebinar.topjava.util.RestaurantTestUtils;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.model.User.Role.ADMIN;
import static ru.javawebinar.topjava.model.User.Role.USER;
import static ru.javawebinar.topjava.util.TestUtils.assertUnprocessableEntity;
import static ru.javawebinar.topjava.util.json.JsonUtils.writeValueToJsonWithAdditionalProp;
import static ru.javawebinar.topjava.util.UserTestUtils.*;

class ProfileRestControllerTest extends AbstractControllerTest {
    private final String URL = "/profile";

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithUserDetails(value = "2_User", userDetailsServiceBeanName = "userRepository")
    void get() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        User actual = TestUtils.readValueFromMvcResult(result, User.class);
        USER_MATCHER.assertMatch(actual, USER_2);
    }

    @Test
    @WithUserDetails(value = "2_User", userDetailsServiceBeanName = "userRepository")
    void update() throws Exception {
        User updated = getUpdated();
        perform(MockMvcRequestBuilders.put(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueToJsonWithAdditionalProp(updated, "password", updated.getPassword())))
                .andExpect(status().isNoContent());

        User actual = userRepository.findById(USER_2_ID).orElseThrow();
        updated.setRole(USER);
        USER_MATCHER.assertMatch(actual, updated);
    }

    @Test
    @WithUserDetails(value = "2_User", userDetailsServiceBeanName = "userRepository")
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(URL))
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(userRepository.findAll(), ADMIN_1, USER_3);
    }

    @Test
    @WithUserDetails(value = "2_User", userDetailsServiceBeanName = "userRepository")
    void getSelectedRestaurant() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL + "/vote"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        RestaurantTO restaurantTO = TestUtils.readValueFromMvcResult(result, RestaurantTO.class);
        RestaurantTestUtils.RESTAURANT_MATCHER.assertMatch(RestaurantUtils.convert(restaurantTO), RestaurantTestUtils.Godzik);
    }

    // not found

    @Test
    @WithUserDetails(value = "2_User", userDetailsServiceBeanName = "userRepository")
    void updateInvalid() throws Exception {
        User user = new User(USER_2_ID, "", "", ADMIN);
        ResultActions actions = perform(MockMvcRequestBuilders.put(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueToJsonWithAdditionalProp(user, "password", user.getPassword())));
        assertUnprocessableEntity(actions, ErrorType.VALIDATION_ERROR);

        List<User> actual = userRepository.findAll();
        USER_MATCHER.assertMatch(actual, ADMIN_1, USER_2, USER_3);

    }

    @Test
    @WithUserDetails(value = "2_User", userDetailsServiceBeanName = "userRepository")
    void updateWithDuplicateName() throws Exception {
        User user = new User(USER_2_ID, USER_3.getName(), "newpass", ADMIN);
        ResultActions actions = perform(MockMvcRequestBuilders.put(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueToJsonWithAdditionalProp(user, "password", user.getPassword())));
        assertUnprocessableEntity(actions, ErrorType.DATA_ERROR);

        List<User> actual = userRepository.findAll();
        USER_MATCHER.assertMatch(actual, ADMIN_1, USER_2, USER_3);
    }

    // unauthorized

    @Test
    void getUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void updateUnauthorized() throws Exception {
        User user = getUpdated();
        perform(MockMvcRequestBuilders.put(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(writeValueToJsonWithAdditionalProp(user, "password", user.getPassword()))))
                .andExpect(status().isUnauthorized());

        USER_MATCHER.assertMatch(userRepository.findAll(), ADMIN_1, USER_2, USER_3);
    }

    @Test
    void deleteUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.delete(URL))
                .andExpect(status().isUnauthorized());

        USER_MATCHER.assertMatch(userRepository.findAll(), ADMIN_1, USER_2, USER_3);
    }

    @Test
    void getSelectedRestaurantUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(URL + "/vote"))
                .andExpect(status().isUnauthorized());
        USER_MATCHER.assertMatch(userRepository.findAll(), ADMIN_1, USER_2, USER_3);
    }
}