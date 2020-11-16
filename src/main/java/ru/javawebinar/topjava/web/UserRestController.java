package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.View;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Transactional(readOnly = true)
@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);

    private final UserRepository repository;

    public UserRestController(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> register(@Validated(View.ValidatedUI.class) @RequestBody User user) {
        LOGGER.info("register user: {}.", user);

        if (!user.isNew()) {
            throw new IllegalRequestDataException("User must be new (id=null)");
        }

        user.setRole(User.Role.USER);
        User created = prepareAndSave(user);

        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping
    public List<User> getAll() {
        LOGGER.info("get all users");
        return repository.findAll();
    }

    @GetMapping("/id{id}")
    public User get(@PathVariable int id) {
        LOGGER.info("get user [{}].", id);
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d could not be found", id)));
    }

    @GetMapping("/{name}")
    public User get(@PathVariable String name) {
        LOGGER.info("get user [{}].", name);
        return repository.findByName(name)
                .orElseThrow(() -> new NotFoundException(String.format("User with name '%s' could not be found", name)));
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/id{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@Validated(View.ValidatedUI.class) @RequestBody User user, @PathVariable int id) {
        LOGGER.info("update user [{}]: {}.", id, user);

        if (user.isNew()) {
            user.setId(id);
        } else {
            if (!Objects.equals(user.getId(), id))
                throw new IllegalRequestDataException("User must be with id=" + id);
        }

        prepareAndSave(user);
    }

    private final PasswordEncoder encoder;

    private User prepareAndSave(User newUser) {
        String name = newUser.getName();
        User user = repository.findByName(name).orElse(newUser);
        if (!user.equals(newUser))
            throw new IllegalRequestDataException(String.format("User with name '%s' already exist!", name));

        String rawPassword = newUser.getPassword();
        String encodedPassword = StringUtils.hasText(rawPassword) ? encoder.encode(rawPassword) : rawPassword;
        newUser.setPassword(encodedPassword);

        return repository.save(newUser);
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/id{id}")
    public void delete(@PathVariable int id) {
        LOGGER.info("delete user [{}].", id);
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("User with id %d could not be found", id));
        }
    }
}
