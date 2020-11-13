package ru.javawebinar.topjava.to;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.beans.ConstructorProperties;

public class DishTO {
    private final Integer id;

    @NotBlank
    @Size(min = 2, max = 32)
    private final String name;

    @NotNull
    private final Long price;

    @ConstructorProperties({"name", "price"})
    public DishTO(@NotBlank @Size(min = 2, max = 32) String name, @NotNull Long price) {
        this(null, name, price);
    }

    public DishTO(Integer id, String name, Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }
}
