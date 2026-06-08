package com.zeki.demo.Unit;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ExternalServiceException;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.exerciseLibrary.client.WgerExerciseClient;
import com.zeki.demo.exerciseLibrary.domain.ExerciseLibrarySearchCriteria;
import com.zeki.demo.exerciseLibrary.entity.ExerciseLibraryEntity;
import com.zeki.demo.exerciseLibrary.entity.ExerciseLibraryItemEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WgerExerciseClientTest {

    private ClientTestServer server;
    private WgerExerciseClient client;

    @BeforeEach
    void setUp() {
        server = new ClientTestServer();
        client = new WgerExerciseClient(WebClient.builder(), server.baseUrl());
    }

    @AfterEach
    void tearDown() {
        server.close();
    }

    @Test
    void searchExercisesBuildsUriFiltersByQueryAndMapsNestedResponse() {
        server.respondWith(path -> ClientTestServer.Response.json(200, exerciseSearchResponse()));

        ExerciseLibraryEntity response = client.searchExercises(filteredSearchRequest());

        assertEquals("/exerciseinfo/?limit=5&offset=2&language=2&muscles=1&equipment=4&category=1", server.requests().get(0));
        assertEquals(1, response.count());
        assertEquals(2, response.total());

        ExerciseLibraryItemEntity item = response.results().get(0);
        assertEquals(10, item.id());
        assertEquals("Dumbbell Curl", item.name());
        assertEquals("curl source", item.descriptionSource());
        assertEquals("Strength", item.category().name());
        assertEquals("Biceps", item.muscles().get(0).nameEn());
        assertEquals("Forearm", item.secondaryMuscles().get(0).name());
        assertEquals("Dumbbell", item.equipment().get(0).name());
        assertEquals(List.of("Arm curl"), item.aliases());
        assertEquals("curl.png", item.images().get(0).image());
        assertEquals("curl.mp4", item.videos().get(0).video());
        assertEquals("CC", item.license().shortName());
        assertEquals("wger", item.licenseAuthor());
    }

    @Test
    void getExerciseByIdMapsNullCollectionsToEmptyValues() {
        server.respondWith(path -> ClientTestServer.Response.json(200, """
                {"id": 20, "uuid": "minimal", "translations": [{"name": "Plank", "language": 2}]}
                """));

        ExerciseLibraryItemEntity item = client.getExerciseById(20);

        assertEquals("/exerciseinfo/20/?language=2", server.requests().get(0));
        assertEquals("Plank", item.name());
        assertTrue(item.muscles().isEmpty());
        assertTrue(item.secondaryMuscles().isEmpty());
        assertTrue(item.equipment().isEmpty());
        assertTrue(item.aliases().isEmpty());
        assertTrue(item.images().isEmpty());
        assertTrue(item.videos().isEmpty());
    }

    @Test
    void searchExercisesUsesOnlyEnglishTranslations() {
        server.respondWith(path -> ClientTestServer.Response.json(200, """
                {
                  "count": 2,
                  "results": [
                    {
                      "id": 30,
                      "uuid": "multi-language",
                      "translations": [
                        {"name": "Sentadilla", "description_source": "Spanish", "language": 4},
                        {"name": "Squat", "description_source": "English", "language": 2}
                      ]
                    },
                    {
                      "id": 31,
                      "uuid": "non-english",
                      "translations": [{"name": "Flexiones", "language": 4}]
                    }
                  ]
                }
                """));

        ExerciseLibraryEntity response = client.searchExercises(emptySearchRequest());

        assertEquals(1, response.count());
        assertEquals("Squat", response.results().get(0).name());
        assertEquals("English", response.results().get(0).descriptionSource());
    }

    @Test
    void referenceEndpointsMapResponsesAndDefaultLimit() {
        server.respondWith(path -> {
            if (path.startsWith("/muscle/")) {
                return ClientTestServer.Response.json(200, """
                        {"results":[{"id":1,"name":"Chest","name_en":"Chest","is_front":true}]}
                        """);
            }
            return ClientTestServer.Response.json(200, """
                    {"results":[{"id":2,"name":"Bench"}]}
                    """);
        });

        assertEquals("Chest", client.getMuscles(null, 3).get(0).name());
        assertEquals("Bench", client.getEquipment(25, null).get(0).name());
        assertEquals("Bench", client.getCategories(50, 5).get(0).name());

        assertEquals("/muscle/?limit=100&offset=3", server.requests().get(0));
        assertEquals("/equipment/?limit=25", server.requests().get(1));
        assertEquals("/exercisecategory/?limit=50&offset=5", server.requests().get(2));
    }

    @Test
    void externalErrorsAreMappedByStatusCode() {
        server.respondWith(path -> ClientTestServer.Response.json(404, "{}"));
        assertInstanceOf(NotFoundException.class, assertThrows(RuntimeException.class, () -> client.getExerciseById(404)));

        server.respondWith(path -> ClientTestServer.Response.json(400, "{}"));
        assertInstanceOf(BadRequestException.class, assertThrows(RuntimeException.class,
                () -> client.searchExercises(emptySearchRequest())));

        server.respondWith(path -> ClientTestServer.Response.json(503, "{}"));
        assertInstanceOf(ExternalServiceException.class, assertThrows(RuntimeException.class,
                () -> client.getCategories(null, null)));
    }

    private ExerciseLibrarySearchCriteria emptySearchRequest() {
        return new ExerciseLibrarySearchCriteria(null, null, null, null, null, null);
    }

    private ExerciseLibrarySearchCriteria filteredSearchRequest() {
        return new ExerciseLibrarySearchCriteria("arm", 5, 2, 1, 4, 1);
    }

    private String exerciseSearchResponse() {
        return """
                {
                  "count": 2,
                  "results": [
                    {
                      "id": 10,
                      "uuid": "exercise-uuid",
                      "category": {"id": 1, "name": "Strength"},
                      "muscles": [{"id": 2, "name": "Biceps", "name_en": "Biceps", "is_front": true,
                        "image_url_main": "main.png", "image_url_secondary": "side.png"}],
                      "muscles_secondary": [{"id": 3, "name": "Forearm", "name_en": "Forearm"}],
                      "equipment": [{"id": 4, "name": "Dumbbell"}],
                      "license": {"id": 5, "full_name": "Creative Commons", "short_name": "CC", "url": "https://license.test"},
                      "license_author": "wger",
                      "images": [{"id": 6, "image": "curl.png", "is_main": true}],
                      "videos": [{"id": 7, "video": "curl.mp4", "is_main": false, "duration": "00:30"}],
                      "translations": [{"name": "Dumbbell Curl", "description": "Lift the weight", "language": 2,
                        "description_source": "curl source", "aliases": [{"alias": "Arm curl"}]}]
                    },
                    {
                      "id": 11,
                      "translations": [{"name": "Squat", "description_source": "legs", "language": 2}]
                    }
                  ]
                }
                """;
    }
}
