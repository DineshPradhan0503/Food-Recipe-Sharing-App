# JWT "Illegal Base64 Character" Error: Analysis and Fix

## 1. The Problem: Why "Illegal base64 character: '-'" Happens

The error "Illegal base64 character: '-'" occurs when a JWT (JSON Web Token) is parsed by a decoder that expects standard Base64 encoding but receives a token encoded with Base64URL.

- **Standard Base64** uses `+`, `/`, and `=` characters. It is designed for encoding binary data in email attachments and other text-based formats.
- **Base64URL** is a URL-safe variant that replaces `+` with `-` and `/` with `_`, and it typically omits padding (`=`). JWTs use this encoding to ensure they can be safely transmitted in URLs without being misinterpreted.

The error you saw was a direct result of the `api-gateway`'s JWT parser attempting to decode the token's signature using a standard Base64 decoder. When it encountered the `-` character, which is valid in Base64URL but not in standard Base64, it threw the exception. This typically happens when using older versions of the `jjwt` library or when the parser is not configured correctly.

## 2. The Solution: A Consistent and Secure JWT Implementation

The fix involved three key areas: aligning dependencies, implementing secure key generation, and modernizing the JWT logic in both the `auth-service` and `api-gateway`.

### a. Dependency Alignment

Both the `auth-service` and `api-gateway` were upgraded to use the modern, modular `jjwt` dependencies (version `0.11.5`). This is the most crucial step, as it ensures both services use the same underlying JWT implementation, which correctly handles Base64URL encoding by default.

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

### b. Secure Key Generation

Both services were updated to use `Keys.hmacShaKeyFor()` to generate a `java.security.Key` from the `jwt.secret`. This is the recommended approach for HMAC-based algorithms like `HS512`. It ensures that the secret string is converted into a cryptographically secure key that is appropriate for the signing algorithm, which prevents signature validation errors.

```java
// JwtUtil.java
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;

// ...

@Value("${jwt.secret}")
private String secret;

private Key key;

@PostConstruct
public void init() {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
}
```

### c. Modernized JWT Logic

-   **Token Generation (`auth-service`)**: The token creation logic was updated to use the modern `Jwts.builder()` with the secure `key` and the `HS512` algorithm.

    ```java
    // auth-service/security/JwtUtil.java
    public String generateToken(User user) {
        // ...
        return Jwts.builder()
                // ...
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    ```

-   **Token Validation (`api-gateway`)**: The token parsing logic was updated to use the modern `Jwts.parserBuilder()` with the same secure `key`. This parser correctly handles Base64URL decoding, which is the direct fix for the error.

    ```java
    // api-gateway/security/JwtUtil.java
    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
    ```

### d. Enhanced Error Handling

The `AuthenticationFilter` was improved to catch `JwtException` specifically. This allows for more granular error handling and provides a structured JSON response for any token-related issues, such as an invalid signature, a malformed token, or an expired token.

```java
// api-gateway/security/AuthenticationFilter.java
try {
    jwtUtil.validateToken(token);
} catch (JwtException e) {
    return onError(exchange, HttpStatus.UNAUTHORIZED, "Token is invalid or expired");
}
```

## 3. JWT Consistency Checklist

To avoid future issues, ensure the following are consistent across all services that handle JWTs:

-   **[ ] Same `jjwt` Version**: All services must use the same version of the `jjwt` library.
-   **[ ] Same `jwt.secret`**: The `jwt.secret` in your `application.properties` or `application.yml` must be identical across all services.
-   **[ ] Same Signing Algorithm**: The token must be signed and verified with the same algorithm (e.g., `HS512`).
-   **[ ] Same Key Generation**: All services should use the same method to generate the `Key` object from the secret string (e.g., `Keys.hmacShaKeyFor()`).
-   **[ ] Same Claims**: The claims you set in the `auth-service` (e.g., `roles`, `id`) must be the same claims you expect to read in the `api-gateway`.

## 4. Postman Testing Flow

To test the fix, follow these steps:

1.  **Start All Services**: Launch the `discovery-server`, `api-gateway`, `auth-service`, and `user-service`.

2.  **Login and Get Token**:
    -   **Method**: `POST`
    -   **URL**: `http://localhost:8080/auth/login` (Note: The request goes through the gateway)
    -   **Body** (raw, JSON):
        ```json
        {
            "email": "your-user@example.com",
            "password": "your-password"
        }
        ```
    -   **Response**: You will receive a JWT token in the response. Copy the entire token string.

3.  **Access a Protected Endpoint**:
    -   **Method**: `GET`
    -   **URL**: `http://localhost:8080/users/profile`
    -   **Headers**:
        -   `Authorization`: `Bearer <your-copied-token>`
    -   **Expected Response**: You should receive a `200 OK` with the user's profile information.

4.  **Test an Admin-Only Endpoint (with a non-admin user)**:
    -   **Method**: `GET`
    -   **URL**: `http://localhost:8080/users/all`
    -   **Headers**:
        -   `Authorization`: `Bearer <your-copied-token>`
    -   **Expected Response**: You should receive a `403 Forbidden` with a JSON error message indicating you do not have the admin role.

5.  **Test with an Invalid Token**:
    -   **Method**: `GET`
    -   **URL**: `http://localhost:8080/users/profile`
    -   **Headers**:
        -   `Authorization`: `Bearer <your-copied-token-with-a-few-characters-changed>`
    -   **Expected Response**: You should receive a `401 Unauthorized` with a JSON error message indicating the token is invalid or expired.
