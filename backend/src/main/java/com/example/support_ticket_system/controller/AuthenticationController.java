package com.example.support_ticket_system.controller;

import com.example.support_ticket_system.model.User;
import com.example.support_ticket_system.service.UserService;
import com.example.support_ticket_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class AuthenticationController {


    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/authenticate")
//    public ResponseEntity<Map<String, String>> createToken(@RequestParam String username, @RequestParam String password) {
//        if ("user".equals(username) && "password".equals(password)) {
//            String token = JwtUtil.generateToken(username);
//            Map<String, String> response = new HashMap<>();
//            response.put("token", token);
//            return ResponseEntity.ok(response); // Return the token in a JSON response
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Return Unauthorized if credentials are invalid
//    }


//    @PostMapping("/authenticate")
//    public ResponseEntity<Map<String, String>> createToken(@RequestBody User user) {
//        // Validate user credentials using the UserService
//
//        System.out.println("Username "+user.getUsername() );
//        System.out.println("Password "+user.getPassword() );
//        if (userService.validateUserCredentials(user.getUsername(), user.getPassword())) {
//
//            String token = JwtUtil.generateToken(user.getUsername());
//            System.out.println("token  "+ token);
//            Map<String, String> response = new HashMap<>();
//            response.put("token", token);
//            return ResponseEntity.ok(response); // Return the token in a JSON response
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Return Unauthorized if credentials are invalid
//    }
//}

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> createToken(@RequestBody User user) {
        // Validate user credentials using the UserService
        if (userService.validateUserCredentials(user.getUsername(), user.getPassword())) {
            String token = JwtUtil.generateToken(user.getUsername());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response); // Return the token in a JSON response
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Return Unauthorized if credentials are invalid
    }
}

