package ru.javawebinar.topjava.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Objects;

@Entity
@Table(name = "dishes", uniqueConstraints = @UniqueConstraint(name = "menu_dish_uniq_idx", columnNames = {"menu_id", "name"}))
public class Dish implements HasId<Integer> {
    @Id
    @SequenceGenerator(name = "dish_seq", sequenceName = "dish_seq", allocationSize = 1, initialValue = 100000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dish_seq")
    private Integer id;

    @NotBlank
    @Size(min = 2, max = 32)
    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @NotNull
    @Column(name = "price", nullable = false)
    private Long price;

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false,
            foreignKey = @ForeignKey(name = "menu_fkey",
                    foreignKeyDefinition = "FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE CASCADE"))
    private Menu menu;

    public Dish() {
    }

    public Dish(String name, Long price) {
        this(null, name, price);
    }

    public Dish(Integer id, String name, Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return id.equals(dish.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", menu=" + (menu == null ? "null" : menu.getDate()) +
                '}';
    }
}
