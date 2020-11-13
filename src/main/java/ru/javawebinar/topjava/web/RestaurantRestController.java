package ru.javawebinar.topjava.web;

import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.View;
import ru.javawebinar.topjava.model.Dish;
import ru.javawebinar.topjava.model.Menu;
import ru.javawebinar.topjava.model.Restaurant;
import ru.javawebinar.topjava.model.Vote;
import ru.javawebinar.topjava.repository.RestaurantRepository;
import ru.javawebinar.topjava.repository.VoteRepository;
import ru.javawebinar.topjava.to.MenuTO;
import ru.javawebinar.topjava.to.RestaurantTO;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.util.exception.TimeUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.UnavailableException;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.RestaurantUtils.convert;

@Transactional(readOnly = true)
@RestController
@RequestMapping(value = "/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantRestController.class);

    private final RestaurantRepository restaurantRepository;

    public RestaurantRestController(RestaurantRepository restaurantRepository, VoteRepository voteRepository) {
        this.restaurantRepository = restaurantRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestaurantTO> register(@Valid @RequestBody RestaurantTO restaurantTO) throws UnavailableException {
        LOGGER.info("register restaurant: {}.", restaurantTO);

        checkTime();
        validateName(restaurantTO.getName());

        restaurantTO.getDishes().forEach(dish -> dish.setId(null));
        Restaurant created = restaurantRepository.save(convert(restaurantTO));
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/restaurants/{name}")
                .buildAndExpand(created).toUri();
        return ResponseEntity.created(uri).body(convert(created));
    }

    @GetMapping
    public List<RestaurantTO> getAll() {
        LOGGER.info("get all restaurants.");
        return convert(restaurantRepository.findAll());
    }

    @GetMapping("/{name}")
    public RestaurantTO get(@PathVariable String name) {
        LOGGER.info("get restaurant [{}].", name);
        return convert(safeGet(name));
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{name}")
    public void rename(@Valid @RequestBody RestaurantTO restaurantTO, @PathVariable String name) {
        LOGGER.info("update restaurant [{}]: {}.", name, restaurantTO);

        validateName(restaurantTO.getName());

        if (restaurantRepository.update(name, restaurantTO.getName()) == 0) {
            throw new NotFoundException(String.format("Restaurant '%s' could not be found", name));
        }
    }

    private void validateName(String name) {
        if (restaurantRepository.findOne(name).isPresent())
            throw new IllegalRequestDataException(String.format("Restaurant with name '%s' already exist!", name));
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{name}")
    public void delete(@PathVariable String name) throws UnavailableException {
        LOGGER.info("delete restaurant [{}].", name);

        checkTime();

        if (restaurantRepository.delete(name) == 0) {
            throw new NotFoundException(String.format("Restaurant '%s' could not be found", name));
        }

    }

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{name}/menu", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateMenu(@PathVariable String name, @Valid @RequestBody MenuTO menuTO) throws UnavailableException {
        LOGGER.info("update restaurant [{}] menu: {}.", name, menuTO);

        checkTime();

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

    private void checkTime() throws UnavailableException {
        LocalTime now = TimeUtils.now().toLocalTime();
        if (now.isAfter(LocalTime.of(10, 0))) {
            throw new UnavailableException("Changes after 10:00 are prohibited",
                    LocalTime.MAX.getSecond() - now.getSecond());
        }
    }

    private Restaurant safeGet(String name) {
        return restaurantRepository.findById(name)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Restaurant '%s' could not be found", name)));
    }

    private final VoteRepository voteRepository;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @PostMapping("/{name}/vote")
    public void vote(@PathVariable String name) throws UnavailableException {
        LOGGER.info("user [{}] votes for restaurant [{}].", ProfileRestController.MOCK_USER.id(), name);

        LocalTime now = TimeUtils.now().toLocalTime();
        if (now.isAfter(LocalTime.of(11, 0))) {
            throw new UnavailableException("The voting service is not available after 11:00",
                    LocalTime.MAX.getSecond() - now.getSecond());
        }

        Menu menu = restaurantRepository.getMenu(name);
        if (menu == null) throw
                new NotFoundException(String.format("Menu for restaurantTO '%s' could not be found", name));

        voteRepository.deleteByUser(ProfileRestController.MOCK_USER.id());
        Vote vote = new Vote(ProfileRestController.MOCK_USER, menu);
        voteRepository.save(vote);
    }

    @GetMapping("/{name}/vote")
    public int getNumOfVotes(@PathVariable String name) {
        LOGGER.info("get the number of votes for the restaurant [{}].", name);
        return voteRepository.getCount(name);
    }

    @JsonView(View.Statistic.class)
    @GetMapping("/statistic")
    public List<RestaurantTO> getStatistic(@RequestParam(value = "from", required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LOGGER.info("get statistic of votes from {}", date == null ? "current date" : date);

        if (date == null) date = TimeUtils.now().toLocalDate();


        return convert(voteRepository.getVotesMap(date));
    }
}
