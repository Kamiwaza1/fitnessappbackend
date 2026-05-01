# Project Overview

## Context and Purpose
FitnessTrackApp Backend is the server-side part of a fitness tracking product. Its purpose is to provide secure API endpoints for user registration, login, profile management, and workout management while persisting application data in PostgreSQL.

Although this repository is backend-only, it supports a broader full-stack use case by exposing the services that a frontend client would need: authentication, data retrieval, input validation, and consistent error handling.

## My Role and Responsibilities
In this project, my responsibilities can be presented as:

- designing and implementing REST endpoints
- building authentication and authorization flows
- modeling domain objects, DTOs, and persistence entities
- setting up validation and exception handling
- configuring local development infrastructure with Docker Compose
- adding testing, coverage, and static-analysis tooling
- iterating on structure and maintainability as the project evolved

## Main Technical Challenges
The project includes several challenges that are valuable to discuss in a portfolio:

- securing endpoints while keeping the API usable for a client application
- separating controller, service, domain, and repository responsibilities
- mapping data cleanly between request models, domain objects, and entities
- validating user input and returning understandable error responses
- keeping development workflow efficient through automation and repeatable setup
- managing refactoring without losing consistency across packages and modules

## Architecture and Boundaries
The backend is built around a layered Spring Boot structure:

- controllers expose HTTP endpoints
- services contain business logic
- domain classes and validators enforce rules
- repositories handle persistence
- security classes manage JWT parsing, authentication, and access control
- exception handlers centralize API error responses

This repository stops at the backend boundary. It does not contain a frontend implementation, but it is structured to support one through documented endpoints, request/response DTOs, authentication tokens, and container-ready deployment setup.

## Why This Project Is Portfolio-Worthy
This project is portfolio-worthy because it demonstrates practical backend engineering rather than isolated exercises. It combines API design, authentication, persistence, validation, testing, static analysis, and environment automation in one codebase.

It is also valuable because it shows realistic software development conditions. The repository contains both strengths and unfinished areas, including active refactoring and a current compile issue. Presenting that honestly makes the project more credible and gives space to discuss decision-making, iteration, and improvement planning instead of only showcasing a perfect final snapshot.
