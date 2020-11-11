package ru.javawebinar.topjava.web;

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

    public static final User MOCK_USER = new User(2, "MOCK", "Pass", User.Role.USER);

    private final UserRestController userRestController;

    public ProfileRestController(UserRestController userRestController, VoteRepository voteRepository) {
        this.userRestController = userRestController;
        this.voteRepository = voteRepository;
    }

    @GetMapping
    public User get() {
        return userRestController.get(MOCK_USER.id());
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody User user) {
        userRestController.update(user, MOCK_USER.id());
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void delete() {
        userRestController.delete(MOCK_USER.id());
    }

    private final VoteRepository voteRepository;

    @GetMapping("/vote")
    public ResponseEntity<RestaurantTO> getSelectedRestaurant() {
        Vote vote = voteRepository.findByUser(MOCK_USER.id());

        if (vote == null || vote.getMenu() == null)
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(RestaurantUtils.convert(vote.getMenu().getRestaurant()));
    }
}
