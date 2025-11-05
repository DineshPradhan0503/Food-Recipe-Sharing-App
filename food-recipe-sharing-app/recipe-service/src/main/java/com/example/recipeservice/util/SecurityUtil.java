package com.example.recipeservice.util;

import com.example.recipeservice.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class SecurityUtil {

    public static void ensureAdmin(String token) {
        List<String> roles = JwtUtil.extractRoles(token);
        if (roles == null || !roles.contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Admin privileges required.");
        }
    }

    public static void ensureCustomer(String token) {
        List<String> roles = JwtUtil.extractRoles(token);
        if (roles == null || (!roles.contains("CUSTOMER") && !roles.contains("ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Customer or Admin privileges required.");
        }
    }
}
