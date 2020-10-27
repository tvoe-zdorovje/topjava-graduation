package ru.javawebinar.topjava.model;

public class User {
    private final Integer id;
    private final String name;
    private final String password;
    private final Role role;

    public User(String name, String password, Role role) {
        this(null, name, password, role);
    }

    public User(Integer id, String name, String password, Role role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public enum Role {
        USER, ADMIN
    }
}
