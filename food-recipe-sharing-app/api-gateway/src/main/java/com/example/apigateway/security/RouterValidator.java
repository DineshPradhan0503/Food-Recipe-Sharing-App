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
