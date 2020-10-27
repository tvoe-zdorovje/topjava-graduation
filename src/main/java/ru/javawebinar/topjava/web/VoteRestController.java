package ru.javawebinar.topjava.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Vote;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/vote", produces = MediaType.APPLICATION_JSON_VALUE)
public class VoteRestController {

    @PostMapping(params = "restaurant")
    public void vote(@RequestParam String restaurant) {

    }

    @GetMapping
    public List<Vote> getAll(@RequestParam(required = false)Date from, @RequestParam(required = false) Date to) {
        return null;
    }
    
    @GetMapping("/{restaurant}")
    public List<Vote> get(@PathVariable String restaurant, @RequestParam(required = false)Date from, @RequestParam(required = false) Date to) {
        return null;
    }

    @GetMapping("/{userId}")
    public List<Vote> get(@PathVariable int userId, @RequestParam(required = false)Date from, @RequestParam(required = false) Date to) {
        return null;
    }
}
