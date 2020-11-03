package ru.javawebinar.topjava.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.ValidationUtils;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.net.URI;
import java.util.List;

@Transactional(readOnly = true)
@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestController {

    private final UserRepository repository;

    public UserRestController(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> register(@RequestBody User user) {
        ValidationUtils.checkNew(user);
        User created = repository.save(user);

        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping
    public List<User> getAll() {
        return repository.findAll();
    }

    @GetMapping("/id{id}")
    public User get(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d could not be found", id)));
    }

    @GetMapping("/{name}")
    public User get(@PathVariable String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new NotFoundException(String.format("User with name '%s' could not be found", name)));
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/id{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody User user, @PathVariable int id) {
        ValidationUtils.assureIdConsistent(user, id);
        repository.save(user);
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/id{id}")
    public void delete(@PathVariable int id) {
        repository.deleteById(id);
    }
}
