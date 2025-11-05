# `ERR_CONNECTION_REFUSED`: Diagnostic and Troubleshooting Guide

## 1. The Problem: Why `ERR_CONNECTION_REFUSED` Happens

The error `net::ERR_CONNECTION_REFUSED` is a low-level networking error that indicates your browser was unable to establish a connection with the server. This is different from a CORS error (which happens after a connection is made). The most common causes are:

-   **The server is not running.** This is the most likely cause.
-   **The server is running on a different port** than the one your frontend is trying to connect to.
-   **A firewall is blocking the connection.**

## 2. Troubleshooting Steps

1.  **Verify that all backend services are running.** You can do this by checking the logs for each service or by using the `ps aux | grep java` command. You should see a separate Java process for each of your microservices.
2.  **Verify the port of the API Gateway.** The `server.port` in your `api-gateway`'s `application.properties` should match the port in your frontend's Axios configuration. By default, this is `8080`.
3.  **Verify the routing in the API Gateway.** Ensure that the `GatewayConfig.java` or `application.yml` in your `api-gateway` has a route that forwards requests from `/auth/**` to the `auth-service`.

## 3. Routing Configuration (YAML Alternative)

If you prefer to use `application.yml` for your routing, here is the equivalent configuration for your `GatewayConfig.java`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/auth/**
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/users/**
          filters:
            - AuthenticationFilter
        - id: recipe-service
          uri: lb://recipe-service
          predicates:
            - Path=/recipes/**
          filters:
            - AuthenticationFilter
        - id: interaction-service
          uri: lb://interaction-service
          predicates:
            - Path=/interactions/**
          filters:
            - AuthenticationFilter
        - id: trending-service
          uri: lb://trending-service
          predicates:
            - Path=/trending/**
          filters:
            - AuthenticationFilter
```

## 4. Postman and Frontend Testing

### a. Postman

1.  **Open Postman and create a new request.**
2.  **Set the method to `POST` and the URL to `http://localhost:8080/auth/login`.**
3.  **In the "Body" tab, select "raw" and "JSON" and enter the following:**
    ```json
    {
        "email": "your-email@example.com",
        "password": "your-password"
    }
    ```
4.  **Send the request.** You should receive a `200 OK` response with a JWT.

### b. React Frontend

1.  **Ensure your `axiosInstance.js` is configured with the correct base URL:**
    ```javascript
    const axiosInstance = axios.create({
      baseURL: 'http://localhost:8080',
    });
    ```
2.  **Start your React development server** (`npm run dev`).
3.  **Open your browser's developer tools** and go to the "Network" tab.
4.  **Try to log in.** You should see a successful `POST` request to `http://localhost:8080/auth/login` and a `200 OK` response.

## 5. CORS Verification

If you have followed the steps in this guide and are still having issues, it is possible that you have a CORS issue. The `CorsConfig.java` file in your `api-gateway` should be configured as follows:

```java
package com.example.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig extends CorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
```
