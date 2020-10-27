package ru.javawebinar.topjava.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Dish;
import ru.javawebinar.topjava.model.Restaurant;

import java.util.Date;
import java.util.List;

import static ru.javawebinar.topjava.model.Restaurant.Menu;

@RestController
@RequestMapping(value = "/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantRestController {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Restaurant create(Restaurant restaurant) {
        return null;
    }

    @GetMapping
    public List<Restaurant> getAll() {
        return null;
    }

    @GetMapping("/{restaurant}")
    public Restaurant get(@PathVariable String restaurant) {
        return null;
    }

    @PutMapping(value = "/{restaurant}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateMenu(@PathVariable String restaurant, List<Dish> dishes) {
        Menu menu = new Menu(dishes);
    }

    @GetMapping("/{restaurant}/menu")
    public List<Menu> getMenu(@PathVariable String restaurant, @RequestParam(required = false) Date from, @RequestParam(required = false) Date to) {
        return null;
    }

    @GetMapping("/menu")
    public List<Menu> getMenu(@RequestParam(required = false) Date from, @RequestParam(required = false) Date to) {
        return null;
    }

}
