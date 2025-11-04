package com.example.userservice.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtil {

    public static void ensureAdmin(String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have admin role");
        }
    }

    public static void ensureUser(String roles) {
        if (roles == null || !roles.contains("USER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have user role");
        }
    }
}
