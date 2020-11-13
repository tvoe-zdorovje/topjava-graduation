package ru.javawebinar.topjava.to;

import com.fasterxml.jackson.annotation.JsonView;
import ru.javawebinar.topjava.View;
import ru.javawebinar.topjava.util.exception.TimeUtils;

import javax.validation.Valid;
import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.List;

public final class MenuTO {
    @JsonView(View.Statistic.class)
    private final LocalDate date;

    @Valid
    @JsonView(View.Regular.class)
    private final List<@Valid DishTO> dishes;

    @ConstructorProperties("dishes")
    public MenuTO(List<DishTO> dishes) {
        this(TimeUtils.now().toLocalDate(), dishes);
    }

    public MenuTO(LocalDate date, List<DishTO> dishes) {
        this.date = date;
        this.dishes = dishes;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<DishTO> getDishes() {
        return dishes;
    }

    @Override
    public String toString() {
        return "MenuTO{" +
                "date=" + date +
                ", dishes=" + dishes +
                '}';
    }
}
