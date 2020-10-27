package ru.javawebinar.topjava.model;

import java.util.Date;
import java.util.List;

public class Restaurant {
    private final String name;

    private Menu menu;

    public Restaurant(String name) {
        this(name, null);
    }

    public Restaurant(String name, Menu menu) {
        this.name = name;
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }


    public static class Menu {
        private final Integer id;
        private final List<Dish> dishes;
        private final Date date;

        public Menu(List<Dish> dishes) {
            this(null, dishes);
        }

        public Menu(Integer id, List<Dish> dishes) {
            this.id = id;
            this.dishes = dishes;
            this.date = new Date();
        }

        public Integer getId() {
            return id;
        }

        public List<Dish> getDishes() {
            return dishes;
        }

        public Date getDate() {
            return new Date(date.getTime());
        }
    }
}
