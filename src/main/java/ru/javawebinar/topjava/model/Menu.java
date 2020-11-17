package ru.javawebinar.topjava.model;

import org.hibernate.annotations.BatchSize;
import ru.javawebinar.topjava.util.exception.TimeUtils;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "menu", uniqueConstraints = @UniqueConstraint(name = "date_uniq_idx", columnNames = {"restaurant", "date"}))
public class Menu {
    @Id
    @SequenceGenerator(name = "menu_seq", sequenceName = "menu_seq", allocationSize = 1, initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menu_seq")
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant", nullable = false, foreignKey = @ForeignKey(name = "restaurant_fkey",
            foreignKeyDefinition = "FOREIGN KEY (restaurant) REFERENCES restaurants(name) ON DELETE CASCADE ON UPDATE CASCADE"))
    private Restaurant restaurant;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @BatchSize(size = 200)
    @OrderBy("name")
    private List<@Valid Dish> dishes;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    public Menu() {
        this(new ArrayList<>());
    }

    public Menu(List<Dish> dishes) {
        this(null, dishes);
    }

    public Menu(Integer id, List<Dish> dishes) {
        this.id = id;
        setDishes(dishes);
        this.date = TimeUtils.now().toLocalDate();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        dishes.forEach(dish -> {
            if (dish != null)
                dish.setMenu(this);
        });
        this.dishes = dishes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        if (id == null || menu.id == null) return false;
        return id.equals(menu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", restaurant=" + restaurant.getName() +
                ", dishes=" + dishes +
                ", date=" + date +
                '}';
    }
}
