package ru.javawebinar.topjava.model;

public class Dish {
    private final String name;

    private final Long price;

    public Dish(String name, Long price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }
}
