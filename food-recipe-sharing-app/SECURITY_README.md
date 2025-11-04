# Food Recipe Sharing App - Security Flow

This document explains the security flow of the Food Recipe Sharing App and provides examples of how to test the endpoints with Postman.

## Security Flow

1.  **User Registration**: A new user registers with their email and password.
2.  **User Login**: The user logs in with their email and password.
3.  **JWT Generation**: The `auth-service` authenticates the user and generates a JWT.
4.  **JWT Storage**: The client stores the JWT and sends it with each request in the `Authorization` header.
5.  **API Gateway**: The API Gateway intercepts each request and validates the JWT.
6.  **Header Injection**: The API Gateway extracts the user's email and roles from the JWT and injects them into the request headers (`X-auth-username` and `X-auth-roles`).
7.  **Downstream Services**: The downstream services receive the request and use the information in the headers to perform authorization.

## Testing with Postman

### 1. Register a New User

*   **Method**: `POST`
*   **URL**: `http://localhost:8080/auth/register`
*   **Body** (raw, JSON):
    ```json
    {
        "username": "testuser",
        "email": "testuser@example.com",
        "password": "password",
        "roles": "CUSTOMER"
    }
    ```

### 2. Log In

*   **Method**: `POST`
*   **URL**: `http://localhost:8080/auth/login`
*   **Body** (raw, JSON):
    ```json
    {
        "email": "testuser@example.com",
        "password": "password"
    }
    ```
*   **Response**:
    ```json
    {
        "success": true,
        "message": "Login successful",
        "data": {
            "token": "eyJhbGciOiJIUzI1NiJ9...",
            "message": "Login successful"
        },
        "timestamp": "2025-11-04T05:43:20.863504"
    }
    ```

### 3. Access a Protected Endpoint

*   **Method**: `GET`
*   **URL**: `http://localhost:8080/users`
*   **Headers**:
    *   `Authorization`: `Bearer <your_jwt>`

### 4. Access an Admin Endpoint (as a Customer)

*   **Method**: `GET`
*   **URL**: `http://localhost:8080/users`
*   **Headers**:
    *   `Authorization`: `Bearer <customer_jwt>`
*   **Response**:
    ```json
    {
        "success": false,
        "message": "Forbidden",
        "timestamp": "2025-11-04T05:45:10.123456"
    }
    ```
