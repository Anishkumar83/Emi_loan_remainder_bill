package org.emiloanwithbill.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "this-is-my-secure-jwt-secret-key-1234567890".getBytes()
    );


    private static final long ACCESS_EXPIRATION = 60 * 60 * 1000;


    private static final long REFRESH_EXPIRATION = 7 * 24 * 60 * 60 * 1000;


    public static String generateAccessToken(long userId, String username, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }


    public static String generateRefreshToken(long userId, String username) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }


    public static Jws<Claims> validateToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token);
    }


    public static long extractUserId(String token) {
        return Long.parseLong(validateToken(token).getPayload().getSubject());
    }

    public static String extractUsername(String token) {
        return validateToken(token).getPayload().get("username", String.class);
    }

    public static String extractRole(String token) {
        return validateToken(token).getPayload().get("role", String.class);
    }

    public static boolean isExpired(String token) {
        Date exp = validateToken(token).getPayload().getExpiration();
        return exp.before(new Date());
    }
}
