package com.example.support_ticket_system.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class HomePageController {
    @GetMapping("/")
    public String welcome()
    {
        return "Welcome to Raise My Ticket System!";
    }
}
