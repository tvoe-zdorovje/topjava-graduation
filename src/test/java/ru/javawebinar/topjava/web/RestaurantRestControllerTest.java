package ru.javawebinar.topjava.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.model.Dish;
import ru.javawebinar.topjava.model.Menu;
import ru.javawebinar.topjava.model.Restaurant;
import ru.javawebinar.topjava.repository.RestaurantRepository;
import ru.javawebinar.topjava.util.TestUtils;
import ru.javawebinar.topjava.util.json.JsonUtils;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.util.testData.RestaurantTestData.*;

class RestaurantRestControllerTest extends AbstractControllerTest {
    private static final String URL = "/restaurants/";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void create() throws Exception {
        Restaurant expected = getNewRestaurant();
        MvcResult result = perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(expected)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        assertMatch(result, expected);
    }

    @Test
    void createWithMenu() throws Exception {
        List<Dish> newMenu = getNewMenu();
        Restaurant expected = getNewRestaurant();
        expected.setMenu(newMenu);

        MvcResult result = perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(expected)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        assertMatch(result, expected);
    }

    private void assertMatch(MvcResult result, Restaurant expected) throws java.io.UnsupportedEncodingException {
        Restaurant created = TestUtils.readValueFromMvcResult(result, Restaurant.class);
        Restaurant actual = restaurantRepository.findById(created.getId()).orElseThrow();

        // set actual IDs
        created.getMenu().setId(actual.getMenu().getId());
        expected.getMenu().setId(actual.getMenu().getId());
        List<Dish> actualDishes = actual.getDishes();
        List<Dish> expectedDishes = expected.getDishes();
        for (int i = 0; i < actualDishes.size(); i++) {
            expectedDishes.get(i).setId(actualDishes.get(i).getId());
        }

        RESTAURANT_MATCHER.assertMatch(actual, created);
        RESTAURANT_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void getAll() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Restaurant> actual = TestUtils.readValuesFromMvcResult(result, Restaurant.class);

        // set menu.id to null
        Restaurant burgerQueen = new Restaurant(BurgerQueen.getId()); //no today's menu
        Restaurant godzik = new Restaurant(Godzik.getId(), copy(Godzik.getDishes()));
        Restaurant mcDnlds = new Restaurant(McDnlds.getId(), copy(McDnlds.getDishes()));
        Restaurant x3 = new Restaurant(X3.getId(), copy(X3.getDishes()));

//        RESTAURANT_MATCHER.assertMatch(actual, burgerQueen, godzik, mcDnlds, x3);
    }

    @Test
    void rename() throws Exception {
        Restaurant expected = getNewRestaurant();
        expected.setMenu(Godzik.getMenu());
        perform(put(URL + Godzik.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(expected)))
                .andDo(print())
                .andExpect(status().isNoContent());

        Restaurant actual = restaurantRepository.findById(expected.getId()).orElseThrow();
        RESTAURANT_MATCHER.assertMatch(actual, expected);
        Assertions.assertThat(restaurantRepository.findById(Godzik.getId()).orElse(null)).isNull();
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(URL + Godzik.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        List<Restaurant> actual = restaurantRepository.findAll();
        actual.forEach(restaurant -> {
            if (restaurant.getMenu() == null) restaurant.setMenu(new Menu());
        });

        RESTAURANT_MATCHER.assertMatch(actual, BurgerQueen, McDnlds, X3);
    }

    @Test
    void get() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL + Godzik.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        Restaurant actual = TestUtils.readValueFromMvcResult(result, Restaurant.class);
        Restaurant expected = new Restaurant(Godzik.getId(), copy(Godzik.getDishes()));
        RESTAURANT_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void updateOutdatedMenu() throws Exception {
        updateMenu(BurgerQueen.getId());
    }

    @Test
    void updateActualMenu() throws Exception {
        updateMenu(Godzik.getId());
    }

    private void updateMenu(String restaurant) throws Exception {
        List<Dish> newMenu = getNewMenu();
        Restaurant expected = new Restaurant(restaurant, newMenu);
        perform(put(URL + restaurant + "/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(newMenu)))
                .andDo(print())
                .andExpect(status().isNoContent());

        Restaurant actual = restaurantRepository.findById(restaurant).orElseThrow();
        expected.getMenu().setId(actual.getMenu().getId());
        List<Dish> actualDishes = actual.getDishes();
        List<Dish> expectedDishes = expected.getDishes();
        for (int i = 0; i < actualDishes.size(); i++) {
            expectedDishes.get(i).setId(actualDishes.get(i).getId());
        }

        RESTAURANT_MATCHER.assertMatch(actual, expected);

    }

    @Test
    void getMenu() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL + Godzik.getId() + "/menu"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        Menu actual = TestUtils.readValueFromMvcResult(result, Menu.class);
        Menu expected = Godzik.getMenu();
        Assertions.assertThat(actual.getId()).isEqualTo(expected.getId());
        Assertions.assertThat(actual.getDate()).isEqualTo(expected.getDate());
        Assertions.assertThat(actual.getDishes()).usingElementComparatorIgnoringFields("menu").isEqualTo(expected.getDishes());
    }

    // TODO not found tests
}
