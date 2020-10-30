package ru.javawebinar.topjava.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.User;

@RestController
@RequestMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileRestController {

    private final UserRestController userRestController;

    public ProfileRestController(UserRestController userRestController) {
        this.userRestController = userRestController;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User register(@RequestBody User user) {
        return null;
    }

    @GetMapping
    public User get() {
        return userRestController.get(0); // FIXME: 27.10.2020
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody User user) {
        userRestController.update(user, 0); // FIXME: 27.10.2020
    }

    @DeleteMapping
    public void delete() {
        userRestController.delete(0); // FIXME: 27.10.2020
    }
}
