package ru.javawebinar.topjava.web;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.model.Dish;
import ru.javawebinar.topjava.model.Menu;
import ru.javawebinar.topjava.model.Restaurant;
import ru.javawebinar.topjava.repository.RestaurantRepository;
import ru.javawebinar.topjava.repository.VoteRepository;
import ru.javawebinar.topjava.to.DishTO;
import ru.javawebinar.topjava.to.MenuTO;
import ru.javawebinar.topjava.to.RestaurantTO;
import ru.javawebinar.topjava.util.TestUtils;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.ErrorType;
import ru.javawebinar.topjava.util.json.JsonUtils;
import ru.javawebinar.topjava.util.testData.UserTestData;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.util.RestaurantUtils.convert;
import static ru.javawebinar.topjava.util.TestUtils.assertUnprocessableEntity;
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
                .map(restaurantTO -> new Restaurant(restaurantTO.getName(), restaurantTO.getDishes()))
                .collect(Collectors.toList());

        Restaurant burgerQueen = new Restaurant(BurgerQueen.getName());

        RESTAURANT_MATCHER.assertMatch(actual, burgerQueen, Godzik, McDnlds);
    }

    @Test
    void rename() throws Exception {
        Restaurant expected = getNewRestaurant();
        expected.setMenu(copy(Godzik.getMenu().getDishes())); // menu will be ignored
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
                .content(JsonUtils.writeValueToJson(convert(expected.getMenu()))))
                .andDo(print())
                .andExpect(status().isNoContent());

        return restaurantRepository.findById(expected.getName()).orElseThrow();
    }

    @Autowired
    VoteRepository voteRepository;

    @Test
    void vote() throws Exception {
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
                new RestaurantTO(Godzik.getName(), convert(Godzik.getMenu()), 1),
                new RestaurantTO(McDnlds.getName(), convert(McDnlds.getMenu()), 1)
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
                new RestaurantTO(BurgerQueen.getName(), convert(MENU_31_BurgerQueen), 0),
                new RestaurantTO(Godzik.getName(), convert(MENU_21_Godzik), 2),
                new RestaurantTO(Godzik.getName(), convert(MENU_22_Godzik), 1),
                new RestaurantTO(McDnlds.getName(), convert(MENU_11_McDnlds), 0),
                new RestaurantTO(McDnlds.getName(), convert(MENU_12_McDnlds), 1)
        );

        assertMatch(actual, expected);
    }

    void assertMatch(List<RestaurantTO> actual, List<RestaurantTO> expected) {
        RecursiveComparisonConfiguration configuration =
                RecursiveComparisonConfiguration.builder().withEqualsForType((menu, menu2) -> true, Menu.class).build();
        Assertions.assertThat(actual).usingRecursiveFieldByFieldElementComparator(configuration).isEqualTo(expected);
    }


    // late
    @Test
    void createLate() throws Exception {
        setTime(11, 0);
        assertLate(perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(getNewRestaurant())))));
    }

    @Test
    void updateMenuLate() throws Exception {
        setTime(11, 0);
        assertLate(perform(put(URL + Godzik.getName() + "/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(new Menu(getNewMenu()))))));
    }

    @Test
    void deleteLate() throws Exception {
        setTime(11, 0);
        assertLate(perform(MockMvcRequestBuilders.delete(URL + Godzik.id())));
    }

    @Test
    void voteLate() throws Exception {
        setTime(11, 30);
        assertLate(perform(post(URL + McDnlds.getName() + "/vote")));
    }

    void assertLate(ResultActions action) throws Exception {
        MvcResult result = action.andDo(print())
                .andExpect(status().isLocked())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorInfo errorInfo = TestUtils.readValueFromMvcResult(result, ErrorInfo.class);
        Assertions.assertThat(errorInfo.getType()).isEqualTo(ErrorType.TEMPORARILY_UNAVAILABLE.getName());
    }

    // not found tests

    @Test
    void getNotFound() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(URL + NOT_FOUNT_NAME));
        assertUnprocessableEntity(actions, ErrorType.DATA_NOT_FOUND);
    }

    @Test
    void renameNotFound() throws Exception {
        ResultActions actions = perform(put(URL + NOT_FOUNT_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(getNewRestaurant()))));
        assertUnprocessableEntity(actions, ErrorType.DATA_NOT_FOUND);
    }

    @Test
    void deleteNotFound() throws Exception {
        setTime(9, 0);
        ResultActions actions = perform(MockMvcRequestBuilders.delete(URL + NOT_FOUNT_NAME));
        assertUnprocessableEntity(actions, ErrorType.DATA_NOT_FOUND);
    }

    @Test
    void updateMenuNotFound() throws Exception {
        setTime(9, 0);
        ResultActions actions = perform(put(URL + NOT_FOUNT_NAME + "/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(new Menu(getNewMenu())))));
        assertUnprocessableEntity(actions, ErrorType.DATA_NOT_FOUND);
    }

    @Test
    void voteNotFound() throws Exception {
        setTime(9, 0);
        ResultActions actions = perform(post(URL + NOT_FOUNT_NAME + "/vote"));
        assertUnprocessableEntity(actions, ErrorType.DATA_NOT_FOUND);
    }


    // invalid

    @Test
    void createWithDuplicateName() throws Exception {
        setTime(9, 0);
        ResultActions actions = perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(Godzik))));
        assertUnprocessableEntity(actions, ErrorType.DATA_ERROR);

        List<Restaurant> actual = restaurantRepository.findAll();
        RESTAURANT_MATCHER.assertMatch(actual, BurgerQueen, Godzik, McDnlds);
    }

    @Test
    void createInvalid() throws Exception {
        setTime(9, 0);
        ResultActions actions = perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(INVALID))));
        assertUnprocessableEntity(actions, ErrorType.VALIDATION_ERROR);

        List<Restaurant> actual = restaurantRepository.findAll();
        RESTAURANT_MATCHER.assertMatch(actual, BurgerQueen, Godzik, McDnlds);
    }

    @Test
    void createWithInvalidMenu() throws Exception {
        setTime(9, 0);
        Restaurant restaurant = new Restaurant("X3", List.of(new Dish("4", 1L), new Dish("Bread", 2L)));
        ResultActions actions = perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(restaurant))));
        assertUnprocessableEntity(actions, ErrorType.VALIDATION_ERROR);

        List<Restaurant> actual = restaurantRepository.findAll();
        RESTAURANT_MATCHER.assertMatch(actual, BurgerQueen, Godzik, McDnlds);
    }

    @Test
    void renameWithDuplicateName() throws Exception {
        setTime(9, 0);
        ResultActions actions = perform(put(URL + Godzik.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(BurgerQueen))));
        assertUnprocessableEntity(actions, ErrorType.DATA_ERROR);

        List<Restaurant> actual = restaurantRepository.findAll();
        RESTAURANT_MATCHER.assertMatch(actual, BurgerQueen, Godzik, McDnlds);
    }

    @Test
    void renameInvalid() throws Exception {
        ResultActions actions = perform(put(URL + Godzik.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(convert(INVALID))));
        assertUnprocessableEntity(actions, ErrorType.VALIDATION_ERROR);

        List<Restaurant> actual = restaurantRepository.findAll();
        RESTAURANT_MATCHER.assertMatch(actual, BurgerQueen, Godzik, McDnlds);
    }

    @Test
    void updateMenuInvalid() throws Exception {
        setTime(9, 0);
        MenuTO menu = new MenuTO(List.of(new DishTO("", 1L), new DishTO("234", 2L)));
        ResultActions actions = perform(put(URL + Godzik.id() + "/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValueToJson(menu)));
        assertUnprocessableEntity(actions, ErrorType.VALIDATION_ERROR);

        List<Restaurant> actual = restaurantRepository.findAll();
        RESTAURANT_MATCHER.assertMatch(actual, BurgerQueen, Godzik, McDnlds);
    }


    // TODO unauth
}
