package com.example.apigateway.security;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final AuthenticationFilter filter;

    public GatewayConfig(AuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("lb://auth-service"))
                .route("user-service", r -> r.path("/users/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-service"))
                .route("recipe-service", r -> r.path("/recipes/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://recipe-service"))
                .route("interaction-service", r -> r.path("/interactions/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://interaction-service"))
                .route("trending-service", r -> r.path("/trending/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://trending-service"))
                .build();
    }
}
