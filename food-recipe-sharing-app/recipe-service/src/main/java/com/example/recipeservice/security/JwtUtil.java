package com.example.recipeservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private static Key key;

    @PostConstruct
    public void init() {
        JwtUtil.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private static Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public static String extractEmail(String token) {
        return getClaims(token.substring(7)).getSubject();
    }

    public static List<String> extractRoles(String token) {
        return (List<String>) getClaims(token.substring(7)).get("roles");
    }

    public static Long extractUserId(String token) {
        return Long.parseLong(getClaims(token.substring(7)).get("id").toString());
    }
}
