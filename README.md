# FitnessTrackApp Backend

## Overview
`fitnessappbackend` is a Spring Boot backend for a fitness tracking application. It provides user authentication, profile management, workout management, validation, persistence, and API documentation for a client application that would consume the service through HTTP.

This repository is backend-only. In a portfolio context, it demonstrates how I built the server-side part of a broader product by combining security, data access, validation, and deployment-oriented tooling into one project.

## Features
- JWT-based authentication with register and login endpoints
- User profile endpoints for reading and updating the authenticated user
- Workout endpoints for creating, reading, updating, and deleting workouts
- Request validation with custom exception handling
- PostgreSQL persistence through Spring Data JPA
- Swagger/OpenAPI annotations for discoverable API documentation
- Docker Compose support for local backend and database startup

## Tech Stack
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Gradle
- Docker Compose
- JaCoCo
- SonarQube
- JUnit 5 and Mockito

## Architecture
The project follows a layered backend structure:

- `controller`: REST endpoints such as auth, users, and workouts
- `service`: business logic and orchestration
- `domain`: validation and domain behavior
- `dto`: request and response models used at the API boundary
- `entity`: persistence models for database storage
- `repository`: data access abstractions and implementations
- `security`: JWT, filter chain, and authentication helpers
- `common/exception`: centralized exception handling

This separation helps keep API concerns, domain rules, persistence, and security responsibilities easier to reason about and test.

## Main API Areas
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/v1/users/me`
- `PUT /api/v1/users/me`
- `GET /api/v1/workouts`
- `GET /api/v1/workouts/{workoutId}`
- `POST /api/v1/workouts`
- `PATCH /api/v1/workouts/{workoutId}`
- `DELETE /api/v1/workouts/{workoutId}`

OpenAPI annotations are present in the controllers, and with `springdoc-openapi-starter-webmvc-ui` included the Swagger UI is expected to be available at:

- `http://localhost:8080/swagger-ui/index.html`

## Local Setup
### Requirements
- Java 21
- Docker and Docker Compose, if you want to run the app with containers

### Run with Gradle
1. Move into the project directory:

```bash
cd fitnessappbackend
```

2. Start a PostgreSQL database locally, or use Docker for the database.

3. Run the application:

```bash
./gradlew bootRun
```

The application uses these defaults from `application.properties`:

- Port: `8080`
- Database: `jdbc:postgresql://localhost:5432/fitness_app`
- Username: `postgres`
- Password: `postgres`
- JWT secret: fallback value provided for local development

You can override them with environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `JWT_SECRET`
- `JWT_EXPIRATION`

### Run with Docker Compose
From the backend directory:

```bash
docker compose up --build
```

This starts:

- a PostgreSQL 16 container
- the Spring Boot backend container on port `8080`

## Quality and Automation
The project includes several quality-focused tools and automation hooks:

- `./gradlew test` runs the JUnit and Mockito test suite
- `./gradlew jacocoTestReport` generates coverage reports
- SonarQube configuration is defined in `build.gradle`
- `.gitlab-ci.yml` contains build, test, quality, and Docker pipeline stages
- `docker compose up --build` provides repeatable local environment setup

These elements are useful both for day-to-day development and for demonstrating software process automation in a portfolio.

## Current Status
This project has solid backend foundations, but it is not in a fully polished state yet.

Current strengths:

- Authentication and authorization are implemented with Spring Security and JWT
- Validation and centralized exception handling are present
- Tests exist for auth and workout-related behavior
- API documentation annotations are already in place
- Local infrastructure automation is configured through Docker Compose and GitLab CI

Current limitations:

- The codebase shows signs of active refactoring, especially around package organization
- There are some duplicated or evolving structures in the user/auth area
- The current build is failing because of a syntax error in `src/main/java/com/zeki/demo/users/mapperDtoDomain/UserDtoDomain.java`

Because of that compile issue, commands such as `./gradlew test` do not currently complete successfully in this checkout.

## Portfolio Relevance
This repository is useful in a portfolio because it shows more than just endpoint creation. It demonstrates how I approached backend architecture, authentication, persistence, testing, containerized setup, and quality tooling while also reflecting honestly on work-in-progress areas. That makes it a strong case study for learning outcomes in full-stack application development, software quality assurance, software process automation, and agile development.

## Related Portfolio Docs
- [Project overview](docs/portfolio/project-overview.md)
- [Learning outcomes](docs/portfolio/learning-outcomes.md)
- [Evidence appendix](docs/portfolio/evidence.md)
