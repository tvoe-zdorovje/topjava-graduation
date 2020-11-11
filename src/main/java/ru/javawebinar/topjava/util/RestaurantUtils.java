package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Menu;
import ru.javawebinar.topjava.model.Restaurant;
import ru.javawebinar.topjava.to.RestaurantTO;
import ru.javawebinar.topjava.util.exception.TimeUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestaurantUtils {

    public static Restaurant convert(RestaurantTO restaurantTO) {
        return new Restaurant(restaurantTO.getName(), restaurantTO.getMenu() == null ? new ArrayList<>() : restaurantTO.getMenu());
    }

    public static RestaurantTO convert(Restaurant restaurant) {
        Menu menu = restaurant.getMenu();
        if (menu == null || !menu.getDate().isEqual(TimeUtils.now().toLocalDate()))
            menu = new Menu();

        return new RestaurantTO(restaurant.getName(), menu.getDishes());
    }

    public static List<RestaurantTO> convert(List<Restaurant> restaurants) {
        return restaurants.stream().map(RestaurantUtils::convert).collect(Collectors.toList());
    }

    public static List<RestaurantTO> convert(Map<Menu, Long> menuVoteMap) {
        return menuVoteMap.entrySet().stream()
                .map(entry -> {
                    Menu menu = entry.getKey();
                    return new RestaurantTO(menu.getRestaurant().getName(), menu.getDishes(), menu.getDate(), entry.getValue());
                })
                .sorted(Comparator.comparing(RestaurantTO::getName))
                .collect(Collectors.toList());
    }

}
