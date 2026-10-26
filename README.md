# DarkLift Backend

A Spring Boot REST and WebSocket API for a fitness tracking application: authentication, workout planning and history, nutrition tracking, exercise discovery, progress analytics, personalised recommendations and live shared workout rooms.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)
![MongoDB](https://img.shields.io/badge/MongoDB-7-47A248)
![Redis](https://img.shields.io/badge/Redis-7-DC382D)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)

This repository contains the backend only. A client application consumes it over HTTP and WebSocket, and the API is documented through OpenAPI.

## Features

- **Authentication and security**: JWT access and refresh tokens delivered in cookies, CSRF protection, email-based password reset with a one-time token plus a 6-digit security code, and account deletion with a grace period.
- **Workouts**: create, update, delete and list workouts, including upcoming workouts, with per-workout exercises and muscle groups.
- **Workout history and progress**: record completed sessions and query progress over time.
- **Nutrition**: log nutrition entries and search foods by name, barcode, brand or category through [Open Food Facts](https://world.openfoodfacts.org).
- **Exercise library**: search exercises, muscles, equipment and categories from [wger](https://wger.de), cached in Redis.
- **Recommendations**: rule-based workout suggestions that a user can start or ignore.
- **Live workouts**: shared rooms joined by room code, with real-time progress updates and chat over STOMP/WebSocket. Chat messages are stored in MongoDB.
- **Quality tooling**: unit and integration tests, JaCoCo coverage, SonarQube analysis and a GitLab CI pipeline.

## Tech stack

| Area | Technology |
| --- | --- |
| Language and framework | Java 21, Spring Boot 4, Spring MVC, Spring WebFlux client, Spring WebSocket |
| Security | Spring Security, JJWT |
| Persistence | PostgreSQL (Spring Data JPA, Flyway migrations), MongoDB (live workout chat) |
| Caching | Redis with Spring Cache |
| API docs | springdoc-openapi (Swagger UI) |
| Build and runtime | Gradle, Docker, Docker Compose |
| Testing and quality | JUnit 5, Mockito, Testcontainers, JaCoCo, SonarQube |

## Architecture

The code is organised by feature. Each feature package follows the same layered structure.

```
src/main/java/com/zeki/demo/
├── users/            authentication, profiles, password reset, account deletion
├── workouts/         workout planning
├── workouthistory/   completed sessions
├── progress/         progress analytics
├── nutrition/        nutrition entries and food search
├── exerciselibrary/  exercise catalogue (wger) with caching
├── recommendations/  workout recommendation engine
├── liveworkouts/     rooms, chat and WebSocket events
├── mail/             transactional email (Resend)
└── common/           shared configuration and exception handling
```

Inside a feature the layers are `controller`, `service`, `domain`, `dto`, `entity` and `repository`. This keeps HTTP concerns, business rules and persistence separate and independently testable. Database schema changes are versioned in `src/main/resources/db/migration` (Flyway).

## API overview

Interactive documentation is served by Swagger UI at `http://localhost:8080/swagger-ui/index.html` once the application is running. Unsafe browser requests require the CSRF header, which `GET /api/v1/auth/csrf` provides.

| Area | Base path | Operations |
| --- | --- | --- |
| Auth | `/api/v1/auth` | `csrf`, `register`, `login`, `refresh`, `logout`, `forgot-password`, `reset-password` |
| Users | `/api/v1/users` | read, update and delete the current user (`/me`), cancel a pending deletion |
| Workouts | `/api/v1/workouts` | list, upcoming, get, create, update, delete |
| Workout history | `/api/v1/workout-history` | record and list sessions, get one session |
| Progress | `/api/v1/progress` | progress summary |
| Nutrition | `/api/v1/nutrition` | entries CRUD, food search by name, barcode, brand and category |
| Exercise library | `/api/v1/exercise-library` | exercise search and detail, muscles, equipment, categories |
| Recommendations | `/api/v1/recommendations` | list, start, ignore |
| Live workouts | `/api/v1/live-workouts` | create, get and delete a room, room details, chat history |

Live workouts also use a WebSocket endpoint at `/ws`. Clients send to the `/app` prefix (`/live-workouts/{roomCode}/join`, `leave`, `progress` and `messages`) and subscribe to `/topic` destinations.

The password reset flow is described in [docs/password-reset.md](docs/password-reset.md).

## Getting started

### Prerequisites

- Java 21
- Docker and Docker Compose

### Run everything with Docker Compose

```bash
docker compose up --build
```

This starts PostgreSQL 16, MongoDB 7, Redis 7 and the backend on port `8080`. Flyway applies the database migrations on startup.

### Run the application with Gradle

Start the databases, then run the application on your machine:

```bash
docker compose up -d postgres mongo redis
./gradlew bootRun
```

### Configuration

Settings are read from environment variables, and every variable has a development default in `application.properties` or `compose.yaml`. To override them, create a `.env` file in the project root (it is git-ignored).

| Variable | Purpose |
| --- | --- |
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | PostgreSQL connection |
| `SPRING_MONGODB_URI` | MongoDB connection |
| `JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET` | Token signing keys (at least 32 characters) |
| `JWT_ACCESS_EXPIRATION`, `JWT_REFRESH_EXPIRATION` | Token lifetimes in milliseconds |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed frontend origins |
| `RESEND_API_KEY`, `MAIL_FROM`, `MAIL_ENABLED` | Transactional email |
| `APP_FRONTEND_URL` | Base URL used in password reset links |
| `PASSWORD_RESET_EXPIRATION_MINUTES` | Lifetime of a reset link |

> The default secrets and credentials are for local development only. Set your own values for any shared or production deployment.

## Testing and quality

```bash
./gradlew test                  # unit and integration tests
./gradlew jacocoTestReport      # coverage report in build/reports/jacoco
```

Integration tests use Testcontainers, so Docker must be running. The `.gitlab-ci.yml` pipeline builds the project, runs the tests, performs SonarQube analysis, builds a Docker image and deploys it.

## Project documentation

- [Password reset flow](docs/password-reset.md)
- [Project overview](docs/portfolio/project-overview.md)
- [Learning outcomes](docs/portfolio/learning-outcomes.md)
- [Evidence appendix](docs/portfolio/evidence.md)

## Author

Built by [Kamiwaza1](https://github.com/Kamiwaza1).
