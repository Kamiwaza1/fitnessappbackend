package com.zeki.demo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zeki.demo.FitnessTrackAppApplication;
import com.zeki.demo.mail.EmailService;
import com.zeki.demo.nutrition.repository.jpa.NutritionRepoJPA;
import com.zeki.demo.users.repository.jpa.UserRepoJPA;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.Map;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FitnessTrackAppApplication.class, properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "mail.enabled=false"
})
@AutoConfigureMockMvc
@Testcontainers
class NutritionIntegrationTest {

    private static final String EMAIL = "nutrition-integration-user@example.com";
    private static final String PASSWORD = "StrongPassword123!";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NutritionRepoJPA nutritionRepoJPA;

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
        nutritionRepoJPA.deleteAll();
        userRepoJPA.deleteAll();
    }

    @Test
    void registeredUserCanManageNutritionEntryLifecycle() throws Exception {
        Cookie accessTokenCookie = registerAndGetAccessTokenCookie();

        MvcResult createResult = mockMvc.perform(post("/api/v1/nutrition")
                        .with(csrf())
                        .cookie(accessTokenCookie)
                        .header("X-Time-Zone", "Europe/Amsterdam")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(Map.of(
                                "mealName", "Protein Breakfast",
                                "mealDate", LocalDate.now().toString(),
                                "calories", 520,
                                "proteinGrams", 42,
                                "carbsGrams", 48,
                                "fatGrams", 16,
                                "notes", "Greek yogurt bowl",
                                "category", "BREAKFAST"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mealName").value("Protein Breakfast"))
                .andExpect(jsonPath("$.calories").value(520))
                .andExpect(jsonPath("$.category").value("BREAKFAST"))
                .andReturn();

        String nutritionId = OBJECT_MAPPER.readTree(createResult.getResponse().getContentAsString())
                .get("nutritionId")
                .asText();

        mockMvc.perform(get("/api/v1/nutrition")
                        .cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nutritionId").value(nutritionId))
                .andExpect(jsonPath("$[0].mealName").value("Protein Breakfast"));

        mockMvc.perform(patch("/api/v1/nutrition/{nutritionId}", nutritionId)
                        .with(csrf())
                        .cookie(accessTokenCookie)
                        .header("X-Time-Zone", "Europe/Amsterdam")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(Map.of(
                                "mealName", "Updated Protein Breakfast",
                                "calories", 610,
                                "notes", "Added oats"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nutritionId").value(nutritionId))
                .andExpect(jsonPath("$.mealName").value("Updated Protein Breakfast"))
                .andExpect(jsonPath("$.calories").value(610))
                .andExpect(jsonPath("$.proteinGrams").value(42));

        mockMvc.perform(delete("/api/v1/nutrition/{nutritionId}", nutritionId)
                        .with(csrf())
                        .cookie(accessTokenCookie)
                        .header("X-Time-Zone", "Europe/Amsterdam"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/nutrition")
                        .cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void nutritionEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/nutrition"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createNutritionRejectsInvalidPayload() throws Exception {
        Cookie accessTokenCookie = registerAndGetAccessTokenCookie();

        mockMvc.perform(post("/api/v1/nutrition")
                        .with(csrf())
                        .cookie(accessTokenCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(Map.of(
                                "mealName", "",
                                "mealDate", LocalDate.now().toString(),
                                "calories", 0,
                                "proteinGrams", 10,
                                "carbsGrams", 10,
                                "fatGrams", 5,
                                "category", "LUNCH"
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private Cookie registerAndGetAccessTokenCookie() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(Map.of(
                                "fullName", "Nutrition Integration User",
                                "email", EMAIL,
                                "password", PASSWORD
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("ACCESS_TOKEN");
        return new Cookie(cookie.getName(), cookie.getValue());
    }
}
