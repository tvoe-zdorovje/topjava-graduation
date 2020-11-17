package ru.javawebinar.topjava.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.exception.ErrorType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.model.User.Role.ADMIN;
import static ru.javawebinar.topjava.util.TestUtils.*;
import static ru.javawebinar.topjava.util.json.JsonUtils.writeValueToJsonWithAdditionalProp;
import static ru.javawebinar.topjava.util.UserTestUtils.*;

class UserRestControllerTest extends AbstractControllerTest {
    private final String URL = "/users/";

    @Autowired
    private UserRepository userRepository;

    @Test
    void register() throws Exception {
        User expected = getNew();
        MvcResult result = perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueToJsonWithAdditionalProp(expected, "password", expected.getPassword())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        User actual = readValueFromMvcResult(result, User.class);
        Integer id = actual.id();

        Assertions.assertThat(id).isNotNull();
        expected.setId(id);

        USER_MATCHER.assertMatch(actual, expected);
        USER_MATCHER.assertMatch(userRepository.findById(id).orElseThrow(), expected);
    }

    @Test
    void getAll() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL).with(httpBasic(USER_2)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        List<User> actual = readValuesFromMvcResult(result, User.class);
        USER_MATCHER.assertMatch(actual, ADMIN_1, USER_2, USER_3);
    }

    @Test
    void get() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL + "id" + USER_2_ID).with(httpBasic(USER_2)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        User actual = readValueFromMvcResult(result, User.class);
        USER_MATCHER.assertMatch(actual, USER_2);
    }

    @Test
    void getByName() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL + USER_2.getName()).with(httpBasic(USER_2)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        User actual = readValueFromMvcResult(result, User.class);
        USER_MATCHER.assertMatch(actual, USER_2);
    }

    @Test
    void update() throws Exception {
        User updated = getUpdated();
        perform(MockMvcRequestBuilders.put(URL + "id" + USER_2_ID).with(httpBasic(ADMIN_1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueToJsonWithAdditionalProp(updated, "password", updated.getPassword())))
                .andExpect(status().isNoContent());

        User actual = userRepository.findById(USER_2_ID).orElseThrow();
        USER_MATCHER.assertMatch(actual, updated);
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(URL + "id" + USER_2_ID).with(httpBasic(ADMIN_1)))
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(userRepository.findAll(), ADMIN_1, USER_3);
    }

    // Not found
    @Test
    void getNotFound() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(URL + "id" + NOT_FOUND_ID).with(httpBasic(ADMIN_1)));
        assertUnprocessableEntity(actions, ErrorType.DATA_NOT_FOUND);
    }

    @Test
    void getByNameNotFound() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(URL + NOT_FOUND_ID).with(httpBasic(ADMIN_1)));
        assertUnprocessableEntity(actions, ErrorType.DATA_NOT_FOUND);
    }

    @Test
    void deleteNotFound() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.delete(URL + "id" + NOT_FOUND_ID).with(httpBasic(ADMIN_1)));
        assertUnprocessableEntity(actions, ErrorType.DATA_NOT_FOUND);
    }

    // invalid

    @Test
    void registerWithDuplicateName() throws Exception {
        User user = new User(USER_2.getName(), "pass");
        ResultActions actions = perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueToJsonWithAdditionalProp(user, "password", user.getPassword())));
        assertUnprocessableEntity(actions, ErrorType.DATA_ERROR);

        List<User> actual = userRepository.findAll();
        USER_MATCHER.assertMatch(actual, ADMIN_1, USER_2, USER_3);
    }

    @Test
    void registerInvalid() throws Exception {
        User user = new User("Name", "   ");
        ResultActions actions = perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueToJsonWithAdditionalProp(user, "password", user.getPassword())));
        assertUnprocessableEntity(actions, ErrorType.VALIDATION_ERROR);

        List<User> actual = userRepository.findAll();
        USER_MATCHER.assertMatch(actual, ADMIN_1, USER_2, USER_3);

    }

    @Test
    void updateInvalid() throws Exception {
        User user = new User(USER_2_ID, "", "", ADMIN);
        ResultActions actions = perform(MockMvcRequestBuilders.put(URL + "id" + USER_2_ID).with(httpBasic(ADMIN_1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueToJsonWithAdditionalProp(user, "password", user.getPassword())));
        assertUnprocessableEntity(actions, ErrorType.VALIDATION_ERROR);

        List<User> actual = userRepository.findAll();
        USER_MATCHER.assertMatch(actual, ADMIN_1, USER_2, USER_3);

    }

    @Test
    void updateWithDuplicateName() throws Exception {
        User user = new User(USER_2_ID, USER_3.getName(), "newpass", ADMIN);
        ResultActions actions = perform(MockMvcRequestBuilders.put(URL + "id" + USER_2_ID).with(httpBasic(ADMIN_1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueToJsonWithAdditionalProp(user, "password", user.getPassword())));
        assertUnprocessableEntity(actions, ErrorType.DATA_ERROR);

        List<User> actual = userRepository.findAll();
        USER_MATCHER.assertMatch(actual, ADMIN_1, USER_2, USER_3);
    }


    // unauthorized & forbidden

    @Test
    void getUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(URL + "id" + USER_2_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerAuth() throws Exception {
        User expected = getNew();
        perform(post(URL).with(httpBasic(ADMIN_1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueToJsonWithAdditionalProp(expected, "password", expected.getPassword())))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateForbidden() throws Exception {
        User user = getUpdated();
        perform(MockMvcRequestBuilders.put(URL + "id" + USER_2_ID).with(httpBasic(USER_2))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueToJsonWithAdditionalProp(user, "password", user.getPassword())))
                .andExpect(status().isForbidden());

        User actual = userRepository.findById(USER_2_ID).orElseThrow();
        USER_MATCHER.assertMatch(actual, USER_2);
    }

    @Test
    void deleteForbidden() throws Exception {
        perform(MockMvcRequestBuilders.delete(URL + "id" + USER_2_ID).with(httpBasic(USER_2)))
                .andExpect(status().isForbidden());

        USER_MATCHER.assertMatch(userRepository.findAll(), ADMIN_1, USER_2, USER_3);
    }

}