package com.example.support_ticket_system.service;

import com.example.support_ticket_system.model.User;
import com.example.support_ticket_system.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private final UserRepository userRepository;

    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }



    // Save or update a user
    public void saveUser(User user) {
        userRepository.saveUser(user);

//        String subject = "Welcome to MoumitaTech!";
//        String body = String.format(
//                "Hello %s,\n\nWelcome to our Support Ticket System! Your role is '%s'.\n\nRegards,\nMoumitaTech Support Team",
//                user.getUsername(), user.getRole());
//        emailService.sendEmail(user.getEmail(), subject, body);
    }

    // Get user by email and role
    public User getUser(String email, String role) {
        return userRepository.getUser(email, role);
    }

    // Delete a user
    public void deleteUser(String email, String role) {
        userRepository.deleteUser(email, role);
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public boolean validateUserCredentials(String username, String password) {



        User user = getUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    public User getUserByUsername(String username) {

        return userRepository.getUserByUsername(username);
    }






}
