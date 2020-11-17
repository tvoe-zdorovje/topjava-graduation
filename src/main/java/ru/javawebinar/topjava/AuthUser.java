package ru.javawebinar.topjava;

import org.springframework.security.core.userdetails.User;

import java.util.Set;

public final class AuthUser extends User {
    private final ru.javawebinar.topjava.model.User user;

    public AuthUser(ru.javawebinar.topjava.model.User user) {
        super(user.getName(), user.getPassword(), Set.of(user.getRole()));
        this.user = user;
    }

    public ru.javawebinar.topjava.model.User getUser() {
        return user;
    }
}
