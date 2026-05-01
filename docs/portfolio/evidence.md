# Evidence Appendix

## Key Evidence by Learning Outcome
### Full-Stack Application
- `src/main/java/com/zeki/demo/users/controller/AuthController.java`
- `src/main/java/com/zeki/demo/users/controller/UserController.java`
- `src/main/java/com/zeki/demo/workouts/controller/WorkoutController.java`
- `src/main/java/com/zeki/demo/users/dto/`
- `src/main/java/com/zeki/demo/workouts/dto/`
- `src/main/java/com/zeki/demo/users/security/`

### Software Quality Assurance
- `src/main/java/com/zeki/demo/users/domain/UserValidator.java`
- `src/main/java/com/zeki/demo/common/exception/GlobalExceptionHandler.java`
- `src/test/java/com/zeki/demo/AuthControllerTest.java`
- `src/test/java/com/zeki/demo/service/AuthServiceTest.java`
- `src/test/java/com/zeki/demo/WorkoutTest.java`
- `build.gradle`

### Software Process Automation
- `build.gradle`
- `.gitlab-ci.yml`
- `compose.yaml`
- `src/main/resources/application.properties`
- `gradlew`

### Agile Development
- `src/main/java/com/zeki/demo/users/`
- `src/main/java/com/zeki/demo/workouts/`
- `.gitlab-ci.yml`
- `compose.yaml`

## Important Commands
Run from `fitnessappbackend/` unless noted otherwise.

```bash
./gradlew bootRun
```

Starts the Spring Boot application locally.

```bash
./gradlew test
```

Runs the automated test suite. In the current checkout, this does not complete because compilation fails first.

```bash
./gradlew jacocoTestReport
```

Generates the JaCoCo coverage report after tests succeed.

```bash
./gradlew jacocoTestReport sonarqube
```

Runs the configured SonarQube analysis flow.

```bash
docker compose up --build
```

Starts the backend and PostgreSQL together in containers.

## Current Status Note
The current checkout has a compilation issue in:

- `src/main/java/com/zeki/demo/users/mapperDtoDomain/UserDtoDomain.java`

Observed failure:

- missing semicolon after the `new User(...)` return expression
- incomplete mapper implementation, which indicates an active refactor in progress

Because of this, Gradle test execution is blocked at compile time.

## Screenshot Placeholders
Add portfolio visuals here later:

- Swagger UI running at `http://localhost:8080/swagger-ui/index.html`
- terminal output for `./gradlew test` or `./gradlew jacocoTestReport`
- JaCoCo HTML coverage report
- SonarQube dashboard
- Docker Compose containers running successfully

## Suggested Talking Points
- How authentication was implemented and secured with JWT
- How DTOs and layered structure support maintainability
- How Docker Compose reduced setup friction
- How quality tooling was introduced into the workflow
- What the current compile issue taught about refactoring discipline
