# Test Assignment

## Overview
This project implements the User class according to specified conditions, creates an API, adds error handling, and covers it with unit tests using Spring.

## Features
- **User Class Implementation**: The User class is implemented with the necessary attributes and methods.
- **API Creation**: An API is created to interact with the User class, allowing operations like creating, updating, deleting, and searching users.
- **Error Handling**: Error handling mechanisms are implemented to provide appropriate responses for different scenarios, such as resource not found or validation errors.
- **Unit Tests**: Unit tests are written for both the controller and the service layers to ensure the functionality works as expected.
- **Custom Validation**: A custom @PastDate annotation is used to validate dates and ensure they are in the past.
- **Swagger UI (OpenAPI)**: OpenAPI (Swagger UI) is integrated into the project for easy API testing and documentation.

## Technologies Used
- Java 17
- Spring Boot
- Spring Boot Starter Web
- Spring Boot Starter Validation
- Spring Boot Starter Test
- Lombok for code generation
- Springdoc OpenAPI for API documentation
- JUnit Vintage for unit testing

## Project Structure
The project structure includes packages for controllers, services, models, and exceptions. The HashMap data structure is used for managing user data, where the email serves as the key.

## Getting Started
1. Clone the repository to your local machine.
2. Ensure you have Java 17 and Maven installed.
3. Build the project using `mvn clean install`.
4. Run the application using `mvn run`.

## API Endpoints
- **GET /api/v1/users**: Get all users.
- **POST /api/v1/users**: Create a new user.
- **PUT /api/v1/users/{email}**: Update a user.
- **PATCH /api/v1/users/{email}**: Partial update of a user.
- **DELETE /api/v1/users/{email}**: Delete a user.
- **GET /api/v1/users/dateRange**: Search for users by birth date range.

## Testing
- Unit tests cover various scenarios for the controller and service layers. Use tools like JUnit and MockMvc for testing.
- Run the application using `mvn test`.

---

For detailed documentation and examples, refer to the [API documentation](http://localhost:8080/swagger-ui/index.html).
