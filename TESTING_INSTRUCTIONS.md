# Testing Instructions to Resolve the 403 Forbidden Error

After applying the fixes, the final and most important step is to ensure your test user has the correct `USER` role in the database. The 403 Forbidden error will persist if your existing test user was created before the roles were being assigned correctly.

## The Solution: Re-register Your Test User

The simplest and cleanest way to resolve this is to **re-register your test user**.

1.  **Delete Your Existing Test User (Optional but Recommended)**
    -   You can do this directly in your `auth_service_db` MySQL database. Find the `user` table and delete the row corresponding to your test user's email.

2.  **Register a New User**
    -   Use the `/auth/register` endpoint to create a new user. This will guarantee that the new user is created with the `USER` role.
    -   **Method**: `POST`
    -   **URL**: `http://localhost:8080/auth/register`
    -   **Body** (raw, JSON):
        ```json
        {
            "username": "your-username",
            "email": "your-email@example.com",
            "password": "your-password"
        }
        ```

3.  **Login and Get a New Token**
    -   Use the `/auth/login` endpoint to get a new JWT.
    -   **Method**: `POST`
    -   **URL**: `http://localhost:8080/auth/login`
    -   **Body** (raw, JSON):
        ```json
        {
            "email": "your-email@example.com",
            "password": "your-password"
        }
        ```

4.  **Test the Protected Endpoints**
    -   Use the new token to access the `/recipes` and `/interactions` endpoints. You should now receive a `200 OK` response.

By following these steps, you will have a test user with the correct role, and the 403 Forbidden error will be resolved.
