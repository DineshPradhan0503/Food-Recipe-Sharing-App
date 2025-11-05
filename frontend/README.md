# Food Recipe Sharing App - Frontend

This is the frontend for the Food Recipe Sharing App, built with React, Redux Toolkit, and TailwindCSS.

## Installation and Setup

1.  **Clone the repository**:
    ```bash
    git clone <your-repo-url>
    ```

2.  **Navigate to the frontend directory**:
    ```bash
    cd frontend
    ```

3.  **Install the dependencies**:
    ```bash
    npm install
    ```

4.  **Run the development server**:
    ```bash
    npm run dev
    ```

    The application will be available at `http://localhost:5173`.

## End-to-End Flow

To run the full application, you will need to have the backend microservices running.

1.  **Start the Backend**:
    -   Start the Eureka Discovery Server.
    -   Start the API Gateway.
    -   Start all other microservices (`auth-service`, `user-service`, `recipe-service`, `interaction-service`, `trending-service`).

2.  **Start the Frontend**:
    -   Navigate to the `frontend` directory and run `npm run dev`.

3.  **Register a New User**:
    -   Go to `http://localhost:5173/register` and create a new account.

4.  **Login**:
    -   Go to `http://localhost:5173/login` and log in with your new account.

5.  **Explore the App**:
    -   You will be redirected to the home page, where you can see the latest and trending recipes.
    -   You can navigate to the recipes page to see all recipes, and you can click on a recipe to see its details.
    -   You can add a new recipe, like and comment on existing recipes, and view and update your profile.

6.  **Admin Features**:
    -   If you have registered a user with the "ADMIN" role, you will be able to access the admin dashboard to moderate recipes and comments.
