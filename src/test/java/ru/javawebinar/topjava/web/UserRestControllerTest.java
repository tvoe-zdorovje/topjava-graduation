package ru.javawebinar.topjava.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.TestUtils;
import ru.javawebinar.topjava.util.json.JsonUtils;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.util.testData.UserTestData.*;

class UserRestControllerTest extends AbstractControllerTest {
    private final String URL = "/users/";

    @Autowired
    UserRepository userRepository;

    @Test
    void register() throws Exception {
        User expected = getNew();
        MvcResult result = perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(expected)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        User actual = TestUtils.readValueFromMvcResult(result, User.class);
        Integer id = actual.getId();

        Assertions.assertThat(id).isNotNull();
        expected.setId(id);

        USER_MATCHER.assertMatch(actual, expected);
        USER_MATCHER.assertMatch(userRepository.findById(id).orElseThrow(), expected);
    }

    @Test
    void getAll() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        List<User> actual = TestUtils.readValuesFromMvcResult(result, User.class);
        USER_MATCHER.assertMatch(actual, ADMIN_1, USER_2, USER_3);
    }

    @Test
    void get() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL+ "id" + USER_2_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        User actual = TestUtils.readValueFromMvcResult(result, User.class);
        USER_MATCHER.assertMatch(actual, USER_2);
    }

    @Test
    void getByName() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL + USER_2.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        User actual = TestUtils.readValueFromMvcResult(result, User.class);
        USER_MATCHER.assertMatch(actual, USER_2);
    }

    @Test
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(URL+ "id" + USER_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(getUpdated())))
                .andDo(print())
                .andExpect(status().isNoContent());

        User actual = userRepository.findById(USER_2_ID).orElseThrow();
        USER_MATCHER.assertMatch(actual, getUpdated());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(URL+ "id" + USER_2_ID))
                .andDo(print())
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(userRepository.findAll(), ADMIN_1, USER_3);
    }

    // TODO Not found
    @Test
    void getNotFound() throws Exception {
        //TODO
    }

    @Test
    void updateNotFound() throws Exception {
        //TODO
    }

    @Test
    void deleteNotFound() throws Exception {
        //TODO
    }

    // TODO unauth
}