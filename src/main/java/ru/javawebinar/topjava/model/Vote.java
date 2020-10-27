package ru.javawebinar.topjava.model;

import java.util.Date;

public class Vote {
    private final Integer id;
    private final User user;
    private final Restaurant restaurant;
    private final Date date;

    public Vote(User user, Restaurant restaurant) {
        this(null, user, restaurant);
    }

    public Vote(Integer id, User user, Restaurant restaurant) {
        this.id = id;
        this.user = user;
        this.restaurant = restaurant;
        this.date = new Date();
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public Date getDate() {
        return new Date(date.getTime());
    }
}