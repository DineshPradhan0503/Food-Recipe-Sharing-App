package com.example.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new RuntimeException("INVALID_TOKEN");
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("EXPIRED_TOKEN");
        }
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return (List<String>) getClaims(token).get("roles");
    }

    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).get("id").toString());
    }
}
