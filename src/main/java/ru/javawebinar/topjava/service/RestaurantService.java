package ru.javawebinar.topjava.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.repository.RestaurantRepository;
import ru.javawebinar.topjava.repository.VoteRepository;
import ru.javawebinar.topjava.to.MenuTO;
import ru.javawebinar.topjava.to.RestaurantTO;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.util.exception.TimeUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

import static ru.javawebinar.topjava.util.RestaurantUtils.convert;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, VoteRepository voteRepository) {
        this.restaurantRepository = restaurantRepository;
        this.voteRepository = voteRepository;
    }

    @CacheEvict(value = "restaurants", allEntries = true)
    public RestaurantTO register(RestaurantTO restaurantTO) {
        return convert(restaurantRepository.save(convert(restaurantTO)));
    }

    @Cacheable("restaurants")
    public List<RestaurantTO> getAll() {
        return convert(restaurantRepository.findAll());
    }

    @Cacheable(value = "restaurants", key = "#name")
    public RestaurantTO get(String name) {
        return convert(safeGet(name));
    }

    @CacheEvict(value = "restaurants", allEntries = true)
    public void rename(RestaurantTO restaurantTO, String name) {
        if (restaurantRepository.update(name, restaurantTO.getName()) == 0) {
            throw new NotFoundException(String.format("Restaurant '%s' could not be found", name));
        }
    }

    @CacheEvict(value = "restaurants", allEntries = true)
    public void delete(String name) {
        if (restaurantRepository.delete(name) == 0) {
            throw new NotFoundException(String.format("Restaurant '%s' could not be found", name));
        }

    }

    @PersistenceContext
    private EntityManager entityManager;

    @CacheEvict(value = "restaurants", allEntries = true)
    public void updateMenu(String name, MenuTO menuTO) {
        Restaurant restaurant = safeGet(name);
        Menu menu = restaurant.getMenu();
        List<Dish> dishes = convert(menuTO).getDishes();

        if (menu.getDate().isEqual(TimeUtils.now().toLocalDate())) {
            entityManager.detach(menu);
            menu.setDishes(dishes);
        } else {
            dishes.forEach(dish -> dish.setId(null));
            restaurant.setMenu(dishes);
        }
        restaurantRepository.save(restaurant);
    }

    private Restaurant safeGet(String name) {
        return restaurantRepository.findById(name)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Restaurant '%s' could not be found", name)));
    }

    public boolean isPresent(String name) {
        return restaurantRepository.findOne(name).isPresent();
    }

    private final VoteRepository voteRepository;

    public void vote(String name, User auth) {
        Menu menu = restaurantRepository.getMenu(name);
        if (menu == null) throw
                new NotFoundException(String.format("Menu for restaurantTO '%s' could not be found", name));

        voteRepository.deleteByUser(auth.id());
        voteRepository.save(new Vote(auth, menu));
    }

    public int getNumOfVotes(String name) {
        return voteRepository.getCount(name);
    }

    public List<RestaurantTO> getStatistic(LocalDate date) {
        return convert(voteRepository.getVotesMap(date));
    }
}
