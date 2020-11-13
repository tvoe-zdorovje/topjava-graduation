package ru.javawebinar.topjava.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import ru.javawebinar.topjava.util.testData.RestaurantTestData;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.model.User.Role.ADMIN;
import static ru.javawebinar.topjava.util.TestUtils.assertUnprocessableEntity;
import static ru.javawebinar.topjava.util.testData.UserTestData.*;

class ProfileRestControllerTest extends AbstractControllerTest{
    private final String URL = "/profile";

    @Autowired
    UserRepository userRepository;

    @Test
    void get() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        User actual = TestUtils.readValueFromMvcResult(result, User.class);
        USER_MATCHER.assertMatch(actual, USER_2);
    }

    @Test
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(getUpdated())))
                .andDo(print())
                .andExpect(status().isNoContent());

        User actual = userRepository.findById(USER_2_ID).orElseThrow();
        USER_MATCHER.assertMatch(actual, getUpdated());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(URL))
                .andDo(print())
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(userRepository.findAll(), ADMIN_1, USER_3);
    }

    @Test
    void getSelectedRestaurant() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL + "/vote"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        RestaurantTO restaurantTO = TestUtils.readValueFromMvcResult(result, RestaurantTO.class);
        RestaurantTestData.RESTAURANT_MATCHER.assertMatch(RestaurantUtils.convert(restaurantTO), RestaurantTestData.Godzik);
    }

    // not found
    @Test
    void updateInvalid() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.put(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(new User(USER_2_ID, "", "", ADMIN))));
        assertUnprocessableEntity(actions, ErrorType.VALIDATION_ERROR);

        List<User> actual = userRepository.findAll();
        USER_MATCHER.assertMatch(actual, ADMIN_1, USER_2, USER_3);

    }

    @Test
    void updateWithDuplicateName() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.put(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(new User(USER_2_ID, USER_3.getName(), "newpass", ADMIN))));
        assertUnprocessableEntity(actions, ErrorType.DATA_ERROR);

        List<User> actual = userRepository.findAll();
        USER_MATCHER.assertMatch(actual, ADMIN_1, USER_2, USER_3);
    }
}