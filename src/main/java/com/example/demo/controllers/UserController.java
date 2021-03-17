package com.example.demo.controllers;

import com.example.demo.models.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @RequestMapping(value = "/getUsers", method = RequestMethod.GET)
    public List<User> users() {


        return List.of(new User(), new User());
    }
}
