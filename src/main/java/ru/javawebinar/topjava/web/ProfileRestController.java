package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.model.Vote;
import ru.javawebinar.topjava.repository.VoteRepository;
import ru.javawebinar.topjava.to.RestaurantTO;
import ru.javawebinar.topjava.util.RestaurantUtils;

import javax.validation.Valid;

@Transactional(readOnly = true)
@RestController
@RequestMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileRestController.class);

    private final UserRestController userRestController;

    public ProfileRestController(UserRestController userRestController, VoteRepository voteRepository) {
        this.userRestController = userRestController;
        this.voteRepository = voteRepository;
    }

    @GetMapping
    public User get(@AuthenticationPrincipal(expression = "user") User auth) {
        LOGGER.info("get user [{}]", auth.getId());
        return userRestController.get(auth.id());
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@Valid @RequestBody User user, @AuthenticationPrincipal(expression = "user") User auth) {
        LOGGER.info("update user [{}]: {}", auth.getId(), user);
        user.setId(auth.getId());
        user.setRole(auth.getRole());
        userRestController.update(user, auth.id());
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void delete(@AuthenticationPrincipal(expression = "user") User auth) {
        LOGGER.info("delete user [{}]", auth.getId());
        userRestController.delete(auth.id());
    }

    private final VoteRepository voteRepository;

    @GetMapping("/vote")
    public ResponseEntity<RestaurantTO> getSelectedRestaurant(@AuthenticationPrincipal(expression = "user") User auth) {
        LOGGER.info("get a user-selected restaurant [{}].", auth.getId());

        Vote vote = voteRepository.findByUser(auth.id());

        if (vote == null || vote.getMenu() == null)
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(RestaurantUtils.convert(vote.getMenu().getRestaurant()));
    }
}
