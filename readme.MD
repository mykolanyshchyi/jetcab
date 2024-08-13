# JetCab: Taxi Fleet Communication System

## Overview

This is a test task provided as part of a technical evaluation. The goal was to develop a software system that implements communication for a taxi fleet company. The system allows taxis to exchange messages with other taxis and the booking center. The application is not intended for production use but is designed to demonstrate good programming principles, object-oriented design, and software patterns.

## Task Requirements

The system implements the following capabilities:
1. **List all taxis** with their status and current location.
2. **Publish a new booking** to all available taxis.
3. **Allow taxis to set their state** (available or booked).
4. **Implement a dashboard or statistics** regarding the bookings.

## Technical Choices

### Spring Boot

I chose to use Spring Boot for this task to showcase code that is closer to production standards. Spring Boot provides a robust framework that supports building scalable and maintainable applications with minimal configuration. Additionally, it allows for easy integration of various components such as REST APIs, WebSocket communication, and transaction management.

### Design Patterns Used

The following design patterns were implemented to ensure the system's maintainability, scalability, and clarity:

1. **Model-View-Controller (MVC)**: Separation of concerns between the data model (entities), the business logic (services), and the user interface (controllers).
2. **Service Layer**: All business logic is encapsulated within service classes, providing a clean interface to controllers and separating business logic from other concerns.
3. **Data Transfer Object (DTO)**: Used to transfer data between layers, ensuring that only necessary data is exposed and reducing coupling between layers.
4. **Mapper Pattern**: Mapping between entities and DTOs, implemented using a mapper class to ensure clean and consistent data transformations.
5. **Repository Pattern**: Abstracts data access logic and provides a clean interface to interact with the data source.

### Additional Features and Considerations

- **WebSocket Integration**: The system uses WebSocket to publish bookings to all available taxis in real-time.
- **Pessimistic Locking**: Ensures that only one taxi can take an order at a time, preventing race conditions.
- **Retry Mechanism**: Implemented using Spring Retry to handle transient failures during message delivery.

## What Could Be Improved

While the application meets the task requirements, the following improvements could be made:

1. **Security**: Implement authentication and authorization to secure the application and ensure that only authorized users can access specific endpoints.
2. **Comprehensive Testing**: Although meaningful unit tests were provided, the test coverage could be extended to include more edge cases, integration tests, and performance testing.
3. **Database Optimization**: Implement indexing on frequently queried fields, such as booking status and timestamps, to improve query performance.
4. **Scalability Considerations**: Implement load balancing, distributed logging, and caching mechanisms to make the system more scalable and performant in a production environment.
5. **Enhanced Error Handling**: Introduce more granular exception handling and better user feedback mechanisms, including the use of custom exceptions and global exception handlers.
6. **Improved UI/UX**: The current implementation focuses primarily on backend functionality. Adding a frontend dashboard to visualize taxi statuses and booking statistics would provide a more comprehensive solution.
7. **Containerization Enhancements**: The current Docker setup is minimal; further enhancements could include multi-stage builds for smaller image sizes and better resource allocation for containerized environments.

## Getting Started

### Prerequisites

- Docker and Docker Compose installed on your machine.

### Running the Application

1. **Clone the repository:**

   ```bash
   git clone git@github.com:mykolanyshchyi/jetcab.git
   cd jetcab
   ```
2. **Build jar file 
```bash
   ./gradlew clean bootJar
   ```
3. **Build and start the application using Docker Compose:**

   ```bash
   docker compose up --build
   ```

4. **Access the application:**

    - The application will be running on `http://localhost:8080`.
    - The H2 database console can be accessed at `http://localhost:8080/h2-console`.

### Reflection

- **Most Interesting Aspect:** The most interesting part of this exercise was integrating real-time communication with taxis using WebSocket, which required careful consideration of concurrency issues.
- **Most Cumbersome Aspect:** Handling the setup and configuration of Docker was the most cumbersome, especially ensuring that all services were correctly containerized.

## Conclusion

This project demonstrates my ability to design and implement a system using modern software engineering principles and best practices. While it is not production-ready, it serves as a solid foundation that could be extended and refined for real-world use.