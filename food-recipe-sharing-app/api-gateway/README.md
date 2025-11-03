# API Gateway Security

## CSRF Protection

Cross-Site Request Forgery (CSRF) protection is disabled in this API Gateway. This is a common practice for stateless APIs that use token-based authentication (like JWT).

CSRF attacks are a concern for web applications that use session-based authentication (e.g., cookies). In a CSRF attack, an attacker tricks a user into making an unintended request to a web application where they are already authenticated.

Since this application uses JWTs for authentication, the client is responsible for storing the token and sending it with each request in the `Authorization` header. The browser does not automatically send the JWT, so the application is not vulnerable to CSRF attacks.

## Securing the Application in Production

In a production environment, it is important to take the following steps to secure the application:

*   **Use a strong secret key for JWTs**: The JWT secret key should be a long, random string that is stored securely. It should not be hardcoded in the application properties.
*   **Use HTTPS**: All communication between the client and the API Gateway, and between the API Gateway and the microservices, should be encrypted using HTTPS.
*   **Implement rate limiting**: To prevent abuse, the API Gateway should implement rate limiting to limit the number of requests that can be made by a single client.
*   **Implement proper logging and monitoring**: All requests to the API Gateway should be logged and monitored for suspicious activity.
