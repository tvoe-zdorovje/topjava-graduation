package ru.javawebinar.topjava.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.User;

@Transactional(readOnly = true)
@RestController
@RequestMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileRestController {

    public static final int USER_ID = 2;

    private final UserRestController userRestController;

    public ProfileRestController(UserRestController userRestController) {
        this.userRestController = userRestController;
    }

    @GetMapping
    public User get() {
        return userRestController.get(USER_ID);
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody User user) {
        userRestController.update(user, USER_ID);
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void delete() {
        userRestController.delete(USER_ID);
    }
}
