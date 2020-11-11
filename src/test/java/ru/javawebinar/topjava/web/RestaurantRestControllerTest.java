package ru.javawebinar.topjava.web;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.model.Dish;
import ru.javawebinar.topjava.model.Menu;
import ru.javawebinar.topjava.model.Restaurant;
import ru.javawebinar.topjava.repository.RestaurantRepository;
import ru.javawebinar.topjava.repository.VoteRepository;
import ru.javawebinar.topjava.to.RestaurantTO;
import ru.javawebinar.topjava.util.TestUtils;
import ru.javawebinar.topjava.util.json.JsonUtils;
import ru.javawebinar.topjava.util.testData.UserTestData;

import javax.servlet.UnavailableException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.util.RestaurantUtils.convert;
import static ru.javawebinar.topjava.util.TestUtils.setTime;
import static ru.javawebinar.topjava.util.testData.RestaurantTestData.*;

class RestaurantRestControllerTest extends AbstractControllerTest {
    private static final String URL = "/restaurants/";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void create() throws Exception {
        Restaurant expected = getNewRestaurant();

        setTime(9, 0);
        MvcResult result = perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(expected))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        assertMatch(result, expected);
    }

    @Test
    void createLate() throws Exception {
        Restaurant expected = getNewRestaurant();

        setTime(11, 0);
        org.junit.jupiter.api.Assertions.assertThrows(UnavailableException.class, () -> perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(expected))))); // TODO refactor after implementing an exception handler
    }

    @Test
    void createWithMenu() throws Exception {
        List<Dish> newMenu = getNewMenu();
        Restaurant expected = getNewRestaurant();
        expected.setMenu(newMenu);

        setTime(9, 0);
        MvcResult result = perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(expected))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        assertMatch(result, expected);
    }

    private void assertMatch(MvcResult result, Restaurant expected) throws java.io.UnsupportedEncodingException {
        RestaurantTO created = TestUtils.readValueFromMvcResult(result, RestaurantTO.class);
        Restaurant actual = restaurantRepository.findById(created.getName()).orElseThrow();

        RESTAURANT_MATCHER.assertMatch(actual, convert(created));
        RESTAURANT_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void getAll() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Restaurant> actual = TestUtils.readValuesFromMvcResult(result, RestaurantTO.class).stream()
                .map(restaurantTO -> new Restaurant(restaurantTO.getName(), restaurantTO.getMenu()))
                .collect(Collectors.toList());

        Restaurant burgerQueen = new Restaurant(BurgerQueen.getName());

        RESTAURANT_MATCHER.assertMatch(actual, burgerQueen, Godzik, McDnlds);
    }

    @Test
    void rename() throws Exception {
        Restaurant expected = getNewRestaurant();
        expected.setMenu(copy(Godzik.getMenu().getDishes()));
        perform(put(URL + Godzik.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(expected))))
                .andDo(print())
                .andExpect(status().isNoContent());

        Restaurant actual = restaurantRepository.findById(expected.id()).orElseThrow();
        RESTAURANT_MATCHER.assertMatch(actual, expected);
        Assertions.assertThat(restaurantRepository.findById(Godzik.id()).orElse(null)).isNull();
    }

    @Test
    void delete() throws Exception {
        setTime(9, 0);
        perform(MockMvcRequestBuilders.delete(URL + Godzik.id()))
                .andDo(print())
                .andExpect(status().isNoContent());

        List<Restaurant> actual = restaurantRepository.findAll();

        RESTAURANT_MATCHER.assertMatch(actual, BurgerQueen, McDnlds);
    }

    @Test
    void deleteLate() throws Exception {
        setTime(11, 0);
        org.junit.jupiter.api.Assertions.assertThrows(UnavailableException.class, () ->
                perform(MockMvcRequestBuilders.delete(URL + Godzik.id()))); // TODO refactor after implementing an exception handler
    }

    @Test
    void get() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL + Godzik.id()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        RestaurantTO actual = TestUtils.readValueFromMvcResult(result, RestaurantTO.class);
        RESTAURANT_MATCHER.assertMatch(convert(actual), Godzik);
    }

    @Test
    void updateOutdatedMenu() throws Exception {
        List<Dish> newMenu = getNewMenu();
        Dish dish = newMenu.get(0);
        dish.setId(MENU_22_Godzik.getDishes().get(0).id());
        Restaurant expected = new Restaurant(BurgerQueen.getName(), newMenu);
        Restaurant actual = updateMenu(expected);

        Assertions.assertThatThrownBy(() -> RESTAURANT_MATCHER.assertMatch(actual, expected));   // dish IDs have been reset
        dish.setId(null);
        RESTAURANT_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void updateActualMenu() throws Exception {
        List<Dish> newMenu = getNewMenu();
        newMenu.get(0).setId(MENU_22_Godzik.getDishes().get(0).id());

        Restaurant expected = new Restaurant(Godzik.getName(), newMenu);
        Restaurant actual = updateMenu(expected);

        RESTAURANT_MATCHER.assertMatch(actual, expected);
    }

    private Restaurant updateMenu(Restaurant expected) throws Exception {
        setTime(9, 0);
        perform(put(URL + expected.getName() + "/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(expected.getMenu().getDishes())))
                .andDo(print())
                .andExpect(status().isNoContent());

        return restaurantRepository.findById(expected.getName()).orElseThrow();
    }

    @Test
    void updateLate() throws IllegalAccessException {
        setTime(11, 0);
        org.junit.jupiter.api.Assertions.assertThrows(UnavailableException.class, () -> perform(put(URL + Godzik.getName() + "/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(Godzik.getMenu().getDishes())))); // TODO refactor after implementing an exception handler
    }

    @Autowired
    VoteRepository voteRepository;

    @Test
    void vote() throws Exception {
        setTime(11, 30);
        org.junit.jupiter.api.Assertions.assertThrows(UnavailableException.class, () -> perform(post(URL + McDnlds.getName() + "/vote"))); // TODO refactor after implementing an exception handler

        Assertions.assertThat(voteRepository.getCount(Godzik.id())).isEqualTo(1);
        Assertions.assertThat(voteRepository.getCount(McDnlds.id())).isEqualTo(1);
        Restaurant initChoice = voteRepository.findByUser(UserTestData.USER_2_ID).getMenu().getRestaurant();
        RESTAURANT_MATCHER.assertMatch(initChoice, Godzik);

        setTime(10, 0);

        perform(post(URL + McDnlds.getName() + "/vote"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Assertions.assertThat(voteRepository.getCount(Godzik.id())).isEqualTo(0);
        Assertions.assertThat(voteRepository.getCount(McDnlds.id())).isEqualTo(2);
        Restaurant currentChoice = voteRepository.findByUser(UserTestData.USER_2_ID).getMenu().getRestaurant();
        RESTAURANT_MATCHER.assertMatch(currentChoice, McDnlds);
    }

    @Test
    void getNumOfVotes() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL + Godzik.getName() + "/vote"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        int count = Integer.parseInt(result.getResponse().getContentAsString());
        Assertions.assertThat(count).isEqualTo(1);
    }

    @Test
    void getTodaysStatistic() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL + "statistic"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        List<RestaurantTO> actual = TestUtils.readValuesFromMvcResult(result, RestaurantTO.class);
        List<RestaurantTO> expected = List.of(
                new RestaurantTO(Godzik.getName(), Godzik.getMenu().getDishes(), Godzik.getMenu().getDate(), 1),
                new RestaurantTO(McDnlds.getName(), McDnlds.getMenu().getDishes(), McDnlds.getMenu().getDate(), 1)
        );

        assertMatch(actual, expected);
    }

    @Test
    void getStatisticFrom() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(URL + "statistic" + "?from=2020-01-01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        List<RestaurantTO> actual = TestUtils.readValuesFromMvcResult(result, RestaurantTO.class);
        List<RestaurantTO> expected = List.of(
                new RestaurantTO(BurgerQueen.getName(), MENU_31_BurgerQueen.getDishes(), MENU_31_BurgerQueen.getDate(), 0),
                new RestaurantTO(Godzik.getName(), MENU_21_Godzik.getDishes(), MENU_21_Godzik.getDate(), 2),
                new RestaurantTO(Godzik.getName(), MENU_22_Godzik.getDishes(), MENU_22_Godzik.getDate(), 1),
                new RestaurantTO(McDnlds.getName(), MENU_11_McDnlds.getDishes(), MENU_11_McDnlds.getDate(), 0),
                new RestaurantTO(McDnlds.getName(), MENU_12_McDnlds.getDishes(), MENU_12_McDnlds.getDate(), 1)
        );

        assertMatch(actual, expected);
    }

    void assertMatch(List<RestaurantTO> actual, List<RestaurantTO> expected) {
        RecursiveComparisonConfiguration configuration =
                RecursiveComparisonConfiguration.builder().withEqualsForType((menu, menu2) -> true, Menu.class).build();
        Assertions.assertThat(actual).usingRecursiveFieldByFieldElementComparator(configuration).isEqualTo(expected);
    }

    // TODO not found tests
}
