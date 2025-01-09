package com.example.support_ticket_system.service;

import com.example.support_ticket_system.model.User;
import com.example.support_ticket_system.repository.UserRepository;
import org.springframework.security.core.Authentication;

public class UserSecurityService {



    private final UserRepository userRepository;

    public UserSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean hasAdminRole(Authentication authentication) {
        System.out.println("HERE");
        String email = authentication.getName();

        System.out.println("EMAIL  "+ email);
        User user = userRepository.getUserByEmail(email); // Fetch user from DynamoDB
        System.out.println("Role  "+ user.getRole());
        return user != null && "ADMIN".equals(user.getRole()); // Check if role is ADMIN
    }
}
