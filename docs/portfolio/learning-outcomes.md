# Learning Outcomes

## Full-Stack Application
### Goal
Demonstrate the ability to contribute meaningfully to a full-stack product by building the backend services that a client application depends on.

### Evidence from This Repo
- Authentication endpoints are implemented in `users/controller/AuthController.java`.
- User profile endpoints are implemented in `users/controller/UserController.java`.
- Workout CRUD endpoints are implemented in `workouts/controller/WorkoutController.java`.
- DTOs define the API boundary for requests and responses in `users/dto` and `workouts/dto`.
- Persistence is handled through JPA entities and repositories in `users/entity`, `workouts/entity`, and `workouts/repository`.
- JWT-based security is configured in `users/security`.

### What I Learned
I learned how backend services support the wider application experience even when the frontend is not in the same repository. Building authentication, domain validation, and API contracts made it clear that full-stack development is not only about user interfaces, but also about dependable backend behavior, secure access, and predictable data exchange.

I also learned the importance of designing endpoints and DTOs in a way that is practical for frontend integration, especially around authentication flows and error handling.

### What I Would Improve Next
I would pair this backend with a frontend client and document the full request flow end-to-end. I would also align naming and package structure further so the backend contract is easier for frontend developers to navigate.

## Software Quality Assurance
### Goal
Demonstrate that software quality is addressed through validation, testing, error handling, security, and maintainable structure.

### Evidence from This Repo
- Password validation logic exists in `users/domain/UserValidator.java`.
- Centralized exception handling exists in `common/exception/GlobalExceptionHandler.java`.
- Security rules are enforced through Spring Security in `users/security/SecurityConfig.java`.
- Unit tests are present in `src/test/java/com/zeki/demo/AuthControllerTest.java`, `src/test/java/com/zeki/demo/service/AuthServiceTest.java`, and `src/test/java/com/zeki/demo/WorkoutTest.java`.
- JaCoCo is configured in `build.gradle` for coverage reporting.
- SonarQube is configured in `build.gradle` for code quality inspection.

### What I Learned
I learned that quality assurance is broader than just writing tests. Validation, access control, exception design, and code organization all influence the reliability of the application. I also learned that tooling only creates value when the codebase is kept buildable and consistent.

An important lesson from this repository is that quality must be maintained continuously during refactoring. The current syntax error in `users/mapperDtoDomain/UserDtoDomain.java` blocks the build, which shows how quickly quality signals and automation lose value when structural changes are not fully completed.

### What I Would Improve Next
I would restore the green build first, then expand automated test coverage around user flows, workout updates, and security edge cases. I would also reduce duplicated or evolving structures so maintainability improves alongside correctness.

## Software Process Automation
### Goal
Show how development, quality checks, and environment setup are supported through automation rather than manual repetition.

### Evidence from This Repo
- Gradle is used for build and test orchestration through `build.gradle` and `gradlew`.
- Automated coverage reporting is configured through the `jacocoTestReport` task.
- SonarQube analysis is configured in `build.gradle`.
- GitLab CI stages are defined in `.gitlab-ci.yml` for build, test, quality, and Docker execution.
- Docker Compose is configured in `compose.yaml` to start the backend and PostgreSQL together.
- Environment-based configuration is used in `src/main/resources/application.properties`.

### What I Learned
I learned that automation improves consistency and reduces setup friction. Using Gradle, CI configuration, and container orchestration makes it easier to reproduce the project across machines and to standardize quality checks.

I also learned that automation depends on a healthy baseline. Even when pipelines and tasks are configured well, a compile failure prevents them from delivering their full benefit. That reinforced the need to keep build stability as a priority.

### What I Would Improve Next
I would strengthen the automation by ensuring the build stays green before merging changes, externalizing sensitive SonarQube configuration, and adding clearer reporting or badges once the pipeline is fully reliable.

## Agile Development
### Goal
Demonstrate iterative development, reflection, adaptation, and continuous improvement through the lifecycle of the project.

### Evidence from This Repo
- The codebase shows iterative growth across authentication, users, workouts, validation, and infrastructure concerns.
- Refactoring is visible in the evolving package organization and overlapping structures in the auth/user area.
- Testing and automation were added alongside feature work instead of being treated only as a final step.
- Docker Compose, CI configuration, and code quality tooling suggest an effort to improve team workflow and feedback cycles.

### What I Learned
I learned that agile development is visible in the way a project changes over time, not only in ceremonies or planning documents. This repository reflects incremental development: features were added, structure evolved, and tooling was introduced to support better feedback.

It also shows one of the harder lessons of agile work: moving quickly through refactors creates risk if consistency is not preserved. The current build issue is an example of where iteration still needs stronger completion and review discipline.

### What I Would Improve Next
I would make smaller refactoring steps, keep the build green after each iteration, and document implementation decisions more consistently as the project evolves. That would strengthen both delivery speed and maintainability.
