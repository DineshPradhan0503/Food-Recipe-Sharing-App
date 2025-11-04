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

            if (routerValidator.isAdmin.test(request)) {
                List<String> roles = jwtUtil.getRoles(token);
                if (!roles.contains("ADMIN")) {
                    return onError(exchange, HttpStatus.FORBIDDEN, "User does not have admin role");
                }
            }

            populateRequestWithHeaders(exchange, token);
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

    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        List<String> roles = jwtUtil.getRoles(token);

        exchange.getRequest().mutate()
                .header("X-User-ID", String.valueOf(userId))
                .header("X-User-Email", username)
                .header("X-User-Roles", String.join(",", roles))
                .build();
    }
}
