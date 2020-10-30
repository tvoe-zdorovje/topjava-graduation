package ru.javawebinar.topjava.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.User;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestController {

    @GetMapping
    public List<User> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        return null;
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody User user, @PathVariable int id) {
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {

    }
}
