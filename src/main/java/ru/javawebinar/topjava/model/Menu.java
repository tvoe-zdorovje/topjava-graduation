package ru.javawebinar.topjava.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu", uniqueConstraints = @UniqueConstraint(name = "date_uniq_idx", columnNames = {"restaurant", "date"}))
public class Menu {
    @Id
    @SequenceGenerator(name = "menu_seq", sequenceName = "menu_seq", allocationSize = 1, initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menu_seq")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "restaurant", nullable = false, foreignKey = @ForeignKey(name = "restaurant_fkey",
            foreignKeyDefinition = "FOREIGN KEY (restaurant) REFERENCES restaurants(name) ON DELETE CASCADE ON UPDATE CASCADE"))
    private Restaurant restaurant;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("name")
    private List<Dish> dishes;

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
        this.date = LocalDate.now(ZoneId.of("America/Montreal"));
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
        dishes.forEach(dish -> dish.setMenu(this));
        this.dishes = dishes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "date=" + date +
                '}';
    }
}
