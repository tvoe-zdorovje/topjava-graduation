package ru.javawebinar.topjava.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.model.Dish;
import ru.javawebinar.topjava.model.Menu;
import ru.javawebinar.topjava.model.Restaurant;
import ru.javawebinar.topjava.repository.RestaurantRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Transactional(readOnly = true)
@RestController
@RequestMapping(value = "/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantRestController {

    private final RestaurantRepository restaurantRepository;

    public RestaurantRestController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> create(@RequestBody Restaurant restaurant) {
        restaurant.getDishes().forEach(dish -> dish.setId(null));
        Restaurant created = restaurantRepository.save(restaurant);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/restaurants/{name}")
                .buildAndExpand(created).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping
    public List<Restaurant> getAll() {
        List<Restaurant> all = restaurantRepository.findAll();
        all.forEach(this::checkMenu);
        return all;
    }

    @GetMapping("/{name}")
    public Restaurant get(@PathVariable String name) {
        Restaurant restaurant = safeGet(name);
        checkMenu(restaurant);
        return restaurant;
    }

    private void checkMenu(Restaurant r) {
        Menu menu = r.getMenu();
        if (menu == null || !menu.getDate().isEqual(LocalDate.now(ZoneId.of("America/Montreal"))))
            r.setMenu(new Menu());
    }


    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{name}")
    public void rename(@RequestBody Restaurant restaurant, @PathVariable String name) {
        if (restaurantRepository.update(name, restaurant.getName()) == 0) {
            throw new NotFoundException(String.format("Restaurant '%s' could not be found", name));
        }
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{name}")
    public void delete(@PathVariable String name) {
        if (restaurantRepository.delete(name) == 0) {
            throw new NotFoundException(String.format("Restaurant '%s' could not be found", name));
        }

    }

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{name}/menu", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateMenu(@PathVariable String name, @RequestBody List<Dish> dishes) {

        Restaurant restaurant = safeGet(name);
        Menu menu = restaurant.getMenu();

        if (menu.getDate().isEqual(LocalDate.now(ZoneId.of("America/Montreal")))) {
            entityManager.detach(menu);
            menu.setDishes(dishes);
        } else {
            dishes.forEach(dish -> dish.setId(null));
            restaurant.setMenu(dishes);
        }
        restaurantRepository.save(restaurant);
    }

    @GetMapping("/{name}/menu")
    public Menu getMenu(@PathVariable String name) {
        Menu menu = safeGet(name).getMenu();
        if (menu == null) throw
                new NotFoundException(String.format("Menu for restaurant '%s' could not be found", name));
        return menu;
    }
    
    private Restaurant safeGet(String name) {
        return restaurantRepository.findById(name)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Restaurant '%s' could not be found", name)));
    }
}
