package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Dish;
import ru.javawebinar.topjava.model.Menu;
import ru.javawebinar.topjava.model.Restaurant;
import ru.javawebinar.topjava.to.DishTO;
import ru.javawebinar.topjava.to.MenuTO;
import ru.javawebinar.topjava.to.RestaurantTO;
import ru.javawebinar.topjava.util.exception.TimeUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RestaurantUtils {

    private RestaurantUtils() {
    }

    public static Restaurant convert(RestaurantTO restaurantTO) {
        return new Restaurant(restaurantTO.getName(), restaurantTO.getDishes() == null ? new ArrayList<>() : restaurantTO.getDishes());
    }

    public static RestaurantTO convert(Restaurant restaurant) {
        Menu menu = restaurant.getMenu();
        if (menu == null || !menu.getDate().isEqual(TimeUtils.now().toLocalDate()))
            menu = new Menu();

        return new RestaurantTO(restaurant.getName(), new MenuTO(menu.getDate(), convert(menu).getDishes()));
    }

    public static List<RestaurantTO> convert(List<Restaurant> restaurants) {
        return restaurants.stream().map(RestaurantUtils::convert).collect(Collectors.toList());
    }

    public static List<RestaurantTO> convert(Map<Menu, Long> menuVoteMap) {
        return menuVoteMap.entrySet().stream()
                .map(entry -> {
                    Menu menu = entry.getKey();
                    return new RestaurantTO(menu.getRestaurant().getName(), convert(menu), entry.getValue());
                })
                .sorted(Comparator.comparing(RestaurantTO::getName))
                .collect(Collectors.toList());
    }

    public static MenuTO convert(Menu menu) {
        List<DishTO> tos = menu.getDishes().stream()
                .map(dish -> new DishTO(dish.getId(), dish.getName(), dish.getPrice()))
                .collect(Collectors.toList());
        return new MenuTO(menu.getDate(), tos);
    }

    public static Menu convert(MenuTO menuTO) {
        List<Dish> dishes = menuTO.getDishes().stream()
                .map(dishTO -> new Dish(dishTO.getId(), dishTO.getName(), dishTO.getPrice()))
                .collect(Collectors.toList());
        return new Menu(dishes);
    }

}
