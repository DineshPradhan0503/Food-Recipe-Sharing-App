# Food Recipe Sharing App

This project is a food recipe sharing application built with a microservices architecture using Spring Boot and Spring Cloud.

## Architecture

The application is composed of the following microservices:

*   **Eureka Discovery Server**: A service registry that allows other microservices to register themselves and discover other services.
*   **API Gateway**: The single entry point for all client requests. It handles routing, security, and JWT validation.
*   **Auth Service**: Handles user registration and login, and generates JWTs for authentication.
*   **User Service**: Manages user profiles.
*   **Recipe Service**: Provides CRUD operations for recipes.
*   **Interaction Service**: Manages user interactions with recipes, such as likes and comments.
*   **Trending Service**: Tracks trending recipes based on user interactions and caches them in Redis.

## Technologies Used

*   **Spring Boot**: For building the microservices.
*   **Spring Cloud**: For building the microservices infrastructure, including the API Gateway and Eureka Discovery Server.
*   **Spring Security**: For handling security and authentication.
*   **Spring Data JPA**: For data persistence.
*   **MySQL**: As the database for each microservice.
*   **Kafka**: For asynchronous communication between microservices.
*   **Redis**: For caching trending recipes.
*   **JWT**: For securing the application.
*   **Maven**: For building the project.

## How to Run

To run the application, you will need to have the following installed:

*   Java 17
*   Maven
*   Docker
*   Docker Compose

1.  **Start the infrastructure**:
    ```bash
    docker-compose up -d
    ```
    This will start the following services:
    *   MySQL
    *   Kafka
    *   Redis
    *   Zookeeper (for Kafka)

2.  **Run each microservice**:
    You can run each microservice by navigating to its directory and running the following command:
    ```bash
    mvn spring-boot:run
    ```
    You will need to run the following services in order:
    1.  `discovery-server`
    2.  `api-gateway`
    3.  `auth-service`
    4.  `user-service`
    5.  `recipe-service`
    6.  `interaction-service`
    7.  `trending-service`

## API Endpoints

All endpoints are accessed through the API Gateway, which runs on port `8080`.

### Auth Service

*   `POST /auth/register`: Register a new user.
*   `POST /auth/login`: Log in and get a JWT.

### User Service

*   `GET /users/{username}`: Get a user's profile.
*   `PUT /users/{username}`: Update a user's profile.

### Recipe Service

*   `GET /recipes`: Get all recipes.
*   `GET /recipes/{id}`: Get a recipe by ID.
*   `POST /recipes`: Create a new recipe.
*   `PUT /recipes/{id}`: Update a recipe.
*   `DELETE /recipes/{id}`: Delete a recipe.

### Interaction Service

*   `GET /interactions/recipe/{recipeId}`: Get all interactions for a recipe.
*   `POST /interactions`: Create a new interaction (like or comment).

### Trending Service

*   `GET /trending`: Get the top 10 trending recipes.
