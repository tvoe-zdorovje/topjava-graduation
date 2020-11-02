package ru.javawebinar.topjava.util.testData;

import org.assertj.core.api.Assertions;
import ru.javawebinar.topjava.model.Dish;
import ru.javawebinar.topjava.model.Menu;
import ru.javawebinar.topjava.model.Restaurant;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;


public class RestaurantTestData {
    public static final Matcher RESTAURANT_MATCHER = new Matcher();

    public static final Menu MENU_11_McDnlds = new Menu(11,
            List.of(new Dish(111, "Cucumber", 2L),
                    new Dish(112, "Orange", 6L),
                    new Dish(113, "Pizza", 15L),
                    new Dish(114, "Tomato", 3L)));

    public static final Menu MENU_12_McDnlds = new Menu(12,
            List.of(new Dish(121, "Burger", 10L),
                    new Dish(122, "Pizza", 16L),
                    new Dish(123, "Pizza XXL", 20L),
                    new Dish(124, "surprise", 5L),
                    new Dish(125, "unknown", 50L)));

    public static final Menu MENU_21_Godzik = new Menu(21,
            List.of(new Dish(211, "Burger", 9L),
                    new Dish(212, "Chicken", 10L),
                    new Dish(213, "Pizza", 21L)));

    public static final Menu MENU_22_Godzik = new Menu(22,
            List.of(new Dish(221, "Chicken", 10L),
                    new Dish(222, "Fish", 8L),
                    new Dish(223, "Mice", 7L)));

    public static final Menu MENU_31_BurgerQueen = new Menu(31,
            List.of(new Dish(311, "Burger", 4L),
                    new Dish(312, "Burger K-K-Kombo", 99L),
                    new Dish(313, "Pizza ;)", 10L),
                    new Dish(314, "Rat", 14L)));

    static {
        LocalDate date = LocalDate.now(ZoneId.of("America/Montreal")).minusDays(1);
        MENU_11_McDnlds.setDate(date);
        MENU_21_Godzik.setDate(date);
        MENU_31_BurgerQueen.setDate(date);
    }

    public static final Restaurant BurgerQueen = new Restaurant("BurgerQueen", MENU_31_BurgerQueen);
    public static final Restaurant Godzik = new Restaurant("Godzik", MENU_22_Godzik);
    public static final Restaurant McDnlds = new Restaurant("McDnlds", MENU_12_McDnlds);
    public static final Restaurant X3 = new Restaurant("X3");

    public static List<Dish> getNewMenu() {
        return List.of(new Dish("New_2", 23L), new Dish("New_3", 32L),
                new Dish(MENU_22_Godzik.getDishes().get(0).getId(), "Updated_1", 12L));
    }

    public static Restaurant getNewRestaurant() {
        return new Restaurant("Texas Burger");
    }

    public static List<Dish> copy(List<Dish> dishes) {
        return dishes.stream().map(dish -> new Dish(dish.getId(), dish.getName(), dish.getPrice())).collect(Collectors.toList());
}

public static class Matcher {
    public Matcher() {
    }

    public void assertMatch(Restaurant actual, Restaurant expected) {
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public void assertMatch(List<Restaurant> actual, Restaurant... expected) {
        assertMatch(actual, List.of(expected));
    }

    public void assertMatch(List<Restaurant> actual, List<Restaurant> expected) {
        Assertions.assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}
}
