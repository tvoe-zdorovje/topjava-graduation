package ru.javawebinar.topjava.to;

import com.fasterxml.jackson.annotation.JsonView;
import ru.javawebinar.topjava.View;
import ru.javawebinar.topjava.model.Dish;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

import static ru.javawebinar.topjava.util.RestaurantUtils.convert;

public final class RestaurantTO {
    @NotBlank
    @Size(min = 2, max = 32)
    @JsonView(View.Regular.class)
    private final String name;

    @Valid
    @JsonView(View.Regular.class)
    private final MenuTO menu;

    @JsonView(View.Statistic.class)
    private final long numOfVotes;

    @ConstructorProperties({"name", "menu"})
    public RestaurantTO(@NotBlank @Size(min = 2, max = 32) String name, MenuTO menu) {
        this.name = name;
        this.menu = menu;
        this.numOfVotes = -1;
    }

    public RestaurantTO(String name, MenuTO menu, long numOfVotes) {
        this.name = name;
        this.menu = menu;
        this.numOfVotes = numOfVotes;
    }

    public String getName() {
        return name;
    }

    public MenuTO getMenu() {
        return menu;
    }

    public List<Dish> getDishes() {
        return menu == null ? new ArrayList<>() : convert(menu).getDishes();
    }

    @Override
    public String toString() {
        return "RestaurantTO{" +
                "name='" + name + '\'' +
                ", menu=" + (menu == null ? "null" : menu) +
                ", numOfVotes=" + numOfVotes +
                '}';
    }
}
