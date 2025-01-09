package com.example.support_ticket_system.auth;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class SharedSecretKey {

    public static final SecretKey SECRET_KEY = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
}
