package ru.javawebinar.topjava.to;

import com.fasterxml.jackson.annotation.JsonView;
import ru.javawebinar.topjava.View;
import ru.javawebinar.topjava.model.Dish;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RestaurantTO {
    @JsonView(View.Regular.class)
    private final String name;

    @JsonView(View.Statistic.class)
    private final LocalDate menuDate;

    @JsonView(View.Regular.class)
    private final List<Dish> menu;

    @JsonView(View.Statistic.class)
    private final long numOfVotes;

    @ConstructorProperties({"name", "menu"})
    public RestaurantTO(String name, List<Dish> menu) {
        this(name, menu == null ? new ArrayList<>() : menu, null, -1L);
    }

    public RestaurantTO(String name, List<Dish> menu, LocalDate menuDate, long numOfVotes) {
        this.name = name;
        this.menuDate = menuDate;
        this.menu = menu;
        this.numOfVotes = numOfVotes;
    }
    public String getName() {
        return name;
    }

    public List<Dish> getMenu() {
        return menu;
    }

    @Override
    public String toString() {
        return "RestaurantTO{" +
                "name='" + name + '\'' +
                ", menuDate=" + menuDate +
                ", menu=" + (menu == null ? "null" : menu) +
                ", numOfVotes=" + numOfVotes +
                '}';
    }
}
