# Comprehensive Validation and Smoke Test Plan

This document provides a complete guide to validating and smoke testing the entire backend of the Food Recipe Sharing App. It includes the full code fix, test data setup, end-to-end flow validation, a smoke testing plan with `curl` commands, and the final project structure.

## 1. The Full Code Fix

This section contains all the corrected code snippets for every file that was changed.

### a. `auth-service`

#### `src/main/java/com/example/authservice/dto/UserDto.java`

```java
package com.example.authservice.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;

public class UserDto {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;
    private List<String> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
```

#### `src/main/java/com/example/authservice/controller/AuthController.java`

```java
package com.example.authservice.controller;

import com.example.authservice.dto.ApiResponse;
import com.example.authservice.dto.JwtResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.UserDto;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> createAuthenticationToken(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        final String jwt = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", new JwtResponse(jwt, "Login successful")));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> saveUser(@Valid @RequestBody UserDto userDto) throws Exception {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Email is already taken", null));
        }
        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        List<String> roles = userDto.getRoles();
        if (roles == null || roles.isEmpty()) {
            newUser.setRoles(Collections.singletonList("CUSTOMER"));
        } else {
            newUser.setRoles(roles);
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", userRepository.save(newUser)));
    }
}
```

### b. `api-gateway`

#### `src/main/java/com/example/apigateway/security/RouterValidator.java`

```java
package com.example.apigateway.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    public static final List<String> publicEndpoints = List.of(
            "/auth/register",
            "/auth/login"
    );

    public static final List<String> customerEndpoints = List.of(
            "/users/profile",
            "/recipes",
            "/interactions"
    );

    public static final List<String> adminEndpoints = List.of(
            "/users/all",
            "/users/delete"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> publicEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().startsWith(uri));

    public Predicate<ServerHttpRequest> isCustomer =
            request -> customerEndpoints.stream()
                    .anyMatch(uri -> request.getURI().getPath().startsWith(uri));

    public Predicate<ServerHttpRequest> isAdmin =
            request -> adminEndpoints.stream()
                    .anyMatch(uri -> request.getURI().getPath().startsWith(uri));
}
```

#### `src/main/java/com/example/apigateway/security/AuthenticationFilter.java`

```java
package com.example.apigateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;
    private final RouterValidator routerValidator;
    private final ObjectMapper objectMapper;

    public AuthenticationFilter(JwtUtil jwtUtil, RouterValidator routerValidator, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.routerValidator = routerValidator;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            if (isAuthMissing(request)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED, "Authorization header is missing");
            }

            final String token = getAuthHeader(request);

            try {
                jwtUtil.validateToken(token);
            } catch (JwtException e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED, "Token is invalid or expired");
            }

            List<String> roles = jwtUtil.getRoles(token);

            if (routerValidator.isAdmin.test(request)) {
                if (!roles.contains("ADMIN")) {
                    return onError(exchange, HttpStatus.FORBIDDEN, "User does not have admin role");
                }
            } else if (routerValidator.isCustomer.test(request)) {
                if (!roles.contains("CUSTOMER") && !roles.contains("ADMIN")) {
                    return onError(exchange, HttpStatus.FORBIDDEN, "User must have CUSTOMER or ADMIN role");
                }
            }
        }
        return chain.filter(exchange);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0).substring(7);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("timestamp", java.time.ZonedDateTime.now().toString());
        errorResponse.put("path", exchange.getRequest().getPath().toString());

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }
}
```

### c. `user-service`, `recipe-service`, `interaction-service`

#### `src/main/java/com/example/{service-name}/util/SecurityUtil.java`

```java
package com.example.userservice.util;

import com.example.userservice.security.JwtUtil;
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
        if (roles == null || !roles.contains("CUSTOMER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Customer privileges required.");
        }
    }
}
```

## 2. Test Data Setup

To properly test the application, you will need to create two users: one with the "ADMIN" role and one with the "CUSTOMER" role.

### a. Create the Admin User

```bash
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "admin123",
    "roles": ["ADMIN"]
}'
```

### b. Create the Customer User

```bash
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{
    "username": "user",
    "email": "user@example.com",
    "password": "user123"
}'
```

## 3. End-to-End Flow Validation

This section describes the step-by-step process to validate the entire user journey.

1.  **Start All Services**: Start the Eureka Server, API Gateway, and all other microservices.
2.  **Register the Admin and Customer Users**: Use the `curl` commands in the "Test Data Setup" section to create the two users.
3.  **Login as the Customer User**:
    ```bash
    curl -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -d '{
        "email": "user@example.com",
        "password": "user123"
    }'
    ```
    Copy the `jwt` token from the response.
4.  **Access a Customer-Protected Endpoint**:
    ```bash
    curl -X GET http://localhost:8080/recipes \
    -H "Authorization: Bearer <your-customer-token>"
    ```
    You should receive a `200 OK` response with a list of recipes.
5.  **Attempt to Access an Admin-Only Endpoint as the Customer User**:
    ```bash
    curl -X GET http://localhost:8080/users/all \
    -H "Authorization: Bearer <your-customer-token>"
    ```
    You should receive a `403 Forbidden` response.
6.  **Login as the Admin User**:
    ```bash
    curl -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -d '{
        "email": "admin@example.com",
        "password": "admin123"
    }'
    ```
    Copy the `jwt` token from the response.
7.  **Access an Admin-Only Endpoint**:
    ```bash
    curl -X GET http://localhost:8080/users/all \
    -H "Authorization: Bearer <your-admin-token>"
    ```
    You should receive a `200 OK` response with a list of all users.
8.  **Access a Customer-Protected Endpoint as the Admin User**:
    ```bash
    curl -X GET http://localhost:8080/recipes \
    -H "Authorization: Bearer <your-admin-token>"
    ```
    You should receive a `200 OK` response with a list of recipes.

## 4. Smoke Testing Plan

This section provides a list of `curl` commands to quickly verify that all key endpoints are functional.

### a. `auth-service`

```bash
# Register a new customer
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{"username": "testuser", "email": "test@example.com", "password": "password"}'

# Login as the new customer
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"email": "test@example.com", "password": "password"}'
```

### b. `recipe-service`

```bash
# Get all recipes (requires a valid token)
curl -X GET http://localhost:8080/recipes -H "Authorization: Bearer <your-token>"

# Create a new recipe (requires a valid token)
curl -X POST http://localhost:8080/recipes -H "Content-Type: application/json" -H "Authorization: Bearer <your-token>" -d '{"name": "Test Recipe", "description": "This is a test recipe", "author": "testuser"}'
```

### c. `interaction-service`

```bash
# Get interactions for a recipe (requires a valid token)
curl -X GET http://localhost:8080/interactions/recipe/1 -H "Authorization: Bearer <your-token>"

# Create a new interaction (requires a valid token)
curl -X POST http://localhost:8080/interactions -H "Content-Type: application/json" -H "Authorization: Bearer <your-token>" -d '{"recipeId": 1, "userId": 1, "type": "LIKE", "comment": "This is a great recipe!"}'
```

### d. `user-service`

```bash
# Get the current user's profile (requires a valid token)
curl -X GET http://localhost:8080/users/profile -H "Authorization: Bearer <your-token>"

# Get all users (requires an admin token)
curl -X GET http://localhost:8080/users/all -H "Authorization: Bearer <your-admin-token>"
```

## 5. Final Project Structure

```
food-recipe-sharing-app
├── api-gateway
├── auth-service
├── discovery-server
├── interaction-service
├── recipe-service
└── user-service
```
