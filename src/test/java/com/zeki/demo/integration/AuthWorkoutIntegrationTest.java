package com.zeki.demo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zeki.demo.FitnessTrackAppApplication;
import com.zeki.demo.mail.EmailService;
import com.zeki.demo.users.repository.jpa.UserRepoJPA;
import com.zeki.demo.workouts.repository.jpa.WorkoutRepoJPA;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FitnessTrackAppApplication.class, properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "mail.enabled=false"
})
@AutoConfigureMockMvc
@Testcontainers
class AuthWorkoutIntegrationTest {

    private static final String EMAIL = "integration-user@example.com";
    private static final String TEST_CREDENTIAL = "StrongPassword123!";
    private static final String WORKOUT_NAME = "Integration Strength";
    private static final String WORKOUTS_ENDPOINT = "/api/v1/workouts";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkoutRepoJPA workoutRepoJPA;

    @Autowired
    private UserRepoJPA userRepoJPA;

    @MockitoBean
    private EmailService emailService;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void cleanDatabase() {
        workoutRepoJPA.deleteAll();
        userRepoJPA.deleteAll();
    }

    @Test
    void registeredUserCanCreateAndListWorkout() throws Exception {
        Cookie accessTokenCookie = registerAndGetAccessTokenCookie();

        mockMvc.perform(post(WORKOUTS_ENDPOINT)
                        .with(csrf())
                        .cookie(accessTokenCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(new CreateWorkoutPayload(
                                WORKOUT_NAME,
                                LocalDate.now().plusDays(1),
                                45,
                                4,
                                8,
                                80.5,
                                "Controlled reps",
                                "Full-body strength session",
                                "STRENGTH",
                                "FULL_BODY",
                                "INTERMEDIATE"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workoutName").value(WORKOUT_NAME))
                .andExpect(jsonPath("$.durationMinutes").value(45))
                .andExpect(jsonPath("$.category").value("STRENGTH"));

        mockMvc.perform(get(WORKOUTS_ENDPOINT)
                        .cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workoutName").value(WORKOUT_NAME))
                .andExpect(jsonPath("$[0].muscleType").value("FULL_BODY"));
    }

    @Test
    void workoutEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get(WORKOUTS_ENDPOINT))
                .andExpect(status().isUnauthorized());
    }

    private Cookie registerAndGetAccessTokenCookie() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(new RegisterPayload(
                                "Integration User",
                                EMAIL,
                                TEST_CREDENTIAL
                        ))))
                .andExpect(status().isCreated())
                .andExpect(cookie().exists("ACCESS_TOKEN"))
                .andExpect(jsonPath("$.user.email").value(EMAIL))
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("ACCESS_TOKEN");
        return new Cookie(cookie.getName(), cookie.getValue());
    }

    private record RegisterPayload(
            String fullName,
            String email,
            String password
    ) {
    }

    private record CreateWorkoutPayload(
            String workoutName,
            LocalDate workoutDate,
            Integer durationMinutes,
            Integer sets,
            Integer reps,
            Double weightKg,
            String notes,
            String description,
            String category,
            String muscleType,
            String difficulty
    ) {
    }
}
