package ru.javawebinar.topjava.web;

import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.View;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.RestaurantService;
import ru.javawebinar.topjava.to.MenuTO;
import ru.javawebinar.topjava.to.RestaurantTO;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.TimeUtils;

import javax.servlet.UnavailableException;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Transactional(readOnly = true)
@RestController
@RequestMapping(value = "/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantRestController.class);

    private final RestaurantService service;

    public RestaurantRestController(RestaurantService service) {
        this.service = service;
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestaurantTO> register(@Valid @RequestBody RestaurantTO restaurantTO) throws UnavailableException {
        LOGGER.info("register restaurant: {}.", restaurantTO);

        checkTime();
        validateName(restaurantTO.getName());

        restaurantTO.getDishes().forEach(dish -> dish.setId(null));
        RestaurantTO created = service.register(restaurantTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/restaurants/{name}")
                .buildAndExpand(created).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping
    public List<RestaurantTO> getAll() {
        LOGGER.info("get all restaurants.");
        return service.getAll();
    }

    @GetMapping("/{name}")
    public RestaurantTO get(@PathVariable String name) {
        LOGGER.info("get restaurant [{}].", name);
        return service.get(name);
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{name}")
    public void rename(@Valid @RequestBody RestaurantTO restaurantTO, @PathVariable String name) {
        LOGGER.info("update restaurant [{}]: {}.", name, restaurantTO);
        validateName(restaurantTO.getName());
        service.rename(restaurantTO, name);
    }

    private void validateName(String name) {
        if (service.isPresent(name))
            throw new IllegalRequestDataException(String.format("Restaurant with name '%s' already exist!", name));
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{name}")
    public void delete(@PathVariable String name) throws UnavailableException {
        LOGGER.info("delete restaurant [{}].", name);
        checkTime();
        service.delete(name);
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{name}/menu", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateMenu(@PathVariable String name, @Valid @RequestBody MenuTO menuTO) throws UnavailableException {
        LOGGER.info("update restaurant [{}] menu: {}.", name, menuTO);
        checkTime();
        service.updateMenu(name, menuTO);
    }

    private void checkTime() throws UnavailableException {
        LocalTime now = TimeUtils.now().toLocalTime();
        if (now.isAfter(LocalTime.of(10, 0))) {
            throw new UnavailableException("Changes after 10:00 are prohibited",
                    LocalTime.MAX.getSecond() - now.getSecond());
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @PostMapping("/{name}/vote")
    public void vote(@PathVariable String name, @AuthenticationPrincipal(expression = "user") User auth) throws UnavailableException {
        LOGGER.info("user [{}] votes for restaurant [{}].", auth.id(), name);

        LocalTime now = TimeUtils.now().toLocalTime();
        if (now.isAfter(LocalTime.of(11, 0))) {
            throw new UnavailableException("The voting service is not available after 11:00",
                    LocalTime.MAX.getSecond() - now.getSecond());
        }
        service.vote(name, auth);
    }

    @GetMapping("/{name}/vote")
    public int getNumOfVotes(@PathVariable String name) {
        LOGGER.info("get the number of votes for the restaurant [{}].", name);
        return service.getNumOfVotes(name);
    }

    @JsonView(View.Statistic.class)
    @GetMapping("/statistics")
    public List<RestaurantTO> getStatistics(@RequestParam(value = "from", required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LOGGER.info("get statistic of votes from {}", date == null ? "current date" : date);
        return service.getStatistic(date == null ? TimeUtils.now().toLocalDate() : date);
    }
}
