package ru.javawebinar.topjava.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Table(name = "restaurants")
public class Restaurant implements HasId<String> {
    @Id
    @Size(min = 2, max = 32)
    @Column(name = "name", unique = true, nullable = false, length = 32)
    private String name;

    @Valid
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "menu", foreignKey = @ForeignKey(name = "restaurant_menu_fk"))
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
        this.menu = menu;
        if (menu != null)
            this.menu.setRestaurant(this);
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
