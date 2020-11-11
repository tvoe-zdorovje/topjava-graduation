package ru.javawebinar.topjava.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "restaurants")
public class Restaurant implements HasId<String> {
    @Id
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "menu", foreignKey = @ForeignKey(name = "menu_fk"))
    private Menu menu;

    public Restaurant() {
        this(null);
    }

    public Restaurant(String name) {
        this(name, new ArrayList<>());
    }

    public Restaurant(String name, List<Dish> menu) {
        this(name, new Menu(menu));
    }

    public Restaurant(String name, Menu menu) {
        this.name = name;
        setMenu(menu);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMenu(List<Dish> menu) {
        setMenu(new Menu(menu));
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        this.menu.setRestaurant(this);
    }

    public Menu getMenu() {
        return menu;
    }

    @Override
    public String getId() {
        return getName();
    }

    @Override
    public void setId(String name) {
        setName(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                ", menu=" + menu +
                '}';
    }
}
