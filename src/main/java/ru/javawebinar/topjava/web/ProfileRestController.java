package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.model.Vote;
import ru.javawebinar.topjava.repository.VoteRepository;
import ru.javawebinar.topjava.to.RestaurantTO;
import ru.javawebinar.topjava.util.RestaurantUtils;

@Transactional(readOnly = true)
@RestController
@RequestMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileRestController.class);

    public static final User MOCK_USER = new User(2, "MOCK", "Pass", User.Role.USER);

    private final UserRestController userRestController;

    public ProfileRestController(UserRestController userRestController, VoteRepository voteRepository) {
        this.userRestController = userRestController;
        this.voteRepository = voteRepository;
    }

    @GetMapping
    public User get() {
        LOGGER.info("get user [{}]", MOCK_USER.getId());
        return userRestController.get(MOCK_USER.id());
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody User user) {
        LOGGER.info("update user [{}]: {}", MOCK_USER.getId(), user);
        userRestController.update(user, MOCK_USER.id());
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void delete() {
        LOGGER.info("delete user [{}]", MOCK_USER.getId());
        userRestController.delete(MOCK_USER.id());
    }

    private final VoteRepository voteRepository;

    @GetMapping("/vote")
    public ResponseEntity<RestaurantTO> getSelectedRestaurant() {
        LOGGER.info("get a user-selected restaurant [{}].", MOCK_USER.getId());

        Vote vote = voteRepository.findByUser(MOCK_USER.id());

        if (vote == null || vote.getMenu() == null)
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(RestaurantUtils.convert(vote.getMenu().getRestaurant()));
    }
}
