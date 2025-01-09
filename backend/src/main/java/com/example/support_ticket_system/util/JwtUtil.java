package com.example.support_ticket_system.util;

import com.example.support_ticket_system.auth.SharedSecretKey;
import com.example.support_ticket_system.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Date;

public class JwtUtil {


    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_2) // Set your desired AWS region
            .build();

    private static final DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    // Create UserRepository instance
    private static final UserRepository userRepository = new UserRepository(dynamoDbEnhancedClient, dynamoDbClient);


    public static String generateToken(String username) {



        String email = userRepository.getEmailByUsername(username); // Implement this method to fetch email by username if needed
        String role = userRepository.getRoleByEmail(email); // Fetch role from DynamoDB
        String userCode= userRepository.getUserCodeByUsername(username);
        String name= userRepository.getNameByEmail(email);



        if (role == null) {
            role = "User"; // Default role if not found
        }


        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)  // Add role claim here
                .claim("name", name) // Add userCode claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token expires in 1 hour
                .signWith(SignatureAlgorithm.HS256, SharedSecretKey.SECRET_KEY)
                .compact();
    }

    // Validate JWT token
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SharedSecretKey.SECRET_KEY) // Set the same secret key used for signing
                    .build()
                    .parseClaimsJws(token); // Parse and validate the token
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract username from JWT token
    public static String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SharedSecretKey.SECRET_KEY) // Set the same secret key used for signing
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SharedSecretKey.SECRET_KEY) // Use the same secret key for parsing
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
