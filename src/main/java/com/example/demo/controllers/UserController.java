package com.example.demo.controllers;

import com.example.demo.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {

        return new ResponseEntity<>(List.of(new User(), new User()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/{login}")
    public ResponseEntity<User> getUserById(@PathVariable String login) {
        // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(new User(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{login}")
    public ResponseEntity deleteUser(@PathVariable String login) {

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
