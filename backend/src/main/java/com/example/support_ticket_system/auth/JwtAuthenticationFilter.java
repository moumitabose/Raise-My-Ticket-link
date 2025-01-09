package com.example.support_ticket_system.auth;

import com.example.support_ticket_system.model.User;
import com.example.support_ticket_system.service.UserService;
import com.example.support_ticket_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JwtAuthenticationFilter  extends OncePerRequestFilter {



    private final UserService userService;

    @Autowired
    public JwtAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//        System.out.println("Authorization Header: " + authHeader);
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            System.out.println("Extracted Token: " + token);
//
//            if (JwtUtil.validateToken(token)) {
//                System.out.println("HERE");
//                String username = JwtUtil.extractUsername(token);
//                System.out.println("Extracted Username: " + username);
//
//                // Extract role from JWT token claims
//                String role = JwtUtil.extractClaims(token).get("role", String.class);
//                System.out.println("Extracted Role: " + role);
//
//                // Convert role to GrantedAuthority for Spring Security
//                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
//
//                // Create authentication token with username and roles
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                        username, null, Collections.singletonList(authority)
//                );
//
//                // Set the authentication in the security context
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } else {
//                System.out.println("Token validation failed.");
//            }
//        } else {
//            System.out.println("Authorization header missing or malformed.");
//        }
//
//        filterChain.doFilter(request, response);
//    }



//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        // Extract the Authorization header from the request
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7); // Remove "Bearer " prefix
//
//            if (JwtUtil.validateToken(token)) {
//
//
//                // Extract email (username) from the token
//                String email = JwtUtil.extractUsername(token);
//                System.out.println("EMAIL "+ email);
//                // Fetch the user from the UserService or UserRepository
//                User user = userService.getUserByEmail(email); // Modify as per your service/repository method
//
//                if (user != null) {
//                    // Extract roles from the user (you can have multiple roles in your application)
//                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
//                            new SimpleGrantedAuthority(user.getRole())
//                    );
//
//                    // Create an authentication token with the user's email and roles
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                            user.getEmail(), null, authorities
//                    );
//
//                    // Set the authentication in the security context
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
//            } else {
//                System.out.println("Token validation failed.");
//            }
//        } else {
//            System.out.println("Authorization header missing or malformed.");
//        }
//
//        // Proceed with the next filter in the chain
//        filterChain.doFilter(request, response);
//    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the Authorization header from the request
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            if (JwtUtil.validateToken(token)) {

                // Extract username from the token (assuming the token contains the username)
                String username = JwtUtil.extractUsername(token);  // This now extracts the username
                System.out.println("USERNAME " + username);

                // Fetch the user from the UserService or UserRepository using username
                User user = userService.getUserByUsername(username); // Modify to fetch user by username

                if (user != null) {
                    System.out.println("USER NOT NULL");
                    System.out.println("role "+ user.getRole());

                    // Extract roles from the user (you can have multiple roles in your application)
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(

                            new SimpleGrantedAuthority("ROLE_" + user.getRole()) // Ensure the role has "ROLE_" prefix
                    );

                    // Create an authentication token with the user's username and roles
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user.getUsername(), null, authorities // Use username here
                    );

                    // Set the authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                System.out.println("Token validation failed.");
            }
        } else {
            System.out.println("Authorization header missing or malformed.");
        }

        // Proceed with the next filter in the chain
        filterChain.doFilter(request, response);
    }

}
