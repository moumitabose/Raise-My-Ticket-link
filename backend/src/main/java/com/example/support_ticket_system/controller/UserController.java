package com.example.support_ticket_system.controller;


import com.example.support_ticket_system.model.Agent;
import com.example.support_ticket_system.model.User;
import com.example.support_ticket_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint to create or update a user
//    @PostMapping("/create")
//    public String saveUser(@RequestBody User user) {
//        userService.saveUser(user);
//        return "User saved successfully!";
//    }
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> saveUser(@RequestBody User user) {
        userService.saveUser(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User added successfully");
        return ResponseEntity.ok(response);
    }

    // Endpoint to retrieve a user by email and role using request body
    @PostMapping("/get")
    public User getUser(@RequestBody User request) {
        return userService.getUser(request.getEmail(), request.getRole());
    }

    // Endpoint to delete a user using request body
    @PostMapping("/delete")
    public String deleteUser(@RequestBody User request) {
        userService.deleteUser(request.getEmail(), request.getRole());
        return "User deleted successfully!";
    }
}
