package ru.javawebinar.topjava.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

import static ru.javawebinar.topjava.model.Restaurant.*;

@Entity
@Table(name = "dishes", uniqueConstraints = @UniqueConstraint(name = "menu_dish_uniq_idx", columnNames = {"menu_id", "name"}))
public class Dish {
    @Id
    @SequenceGenerator(name = "dish_seq", sequenceName = "dish_seq", allocationSize = 1, initialValue = 100000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dish_seq")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false,
            foreignKey = @ForeignKey(name = "menu_fkey",
                    foreignKeyDefinition = "FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE CASCADE"))
    private Menu menu;



    public Dish() {
    }

    public Dish(String name, Long price) {
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
}
