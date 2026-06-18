package com.zeki.demo.exerciseLibrary.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ExternalServiceException;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.exerciseLibrary.domain.ExerciseLibrarySearchCriteria;
import com.zeki.demo.exerciseLibrary.entity.ExerciseImageEntity;
import com.zeki.demo.exerciseLibrary.entity.ExerciseLibraryEntity;
import com.zeki.demo.exerciseLibrary.entity.ExerciseLibraryItemEntity;
import com.zeki.demo.exerciseLibrary.entity.ExerciseLicenseEntity;
import com.zeki.demo.exerciseLibrary.entity.ExerciseMuscleEntity;
import com.zeki.demo.exerciseLibrary.entity.ExerciseReferenceEntity;
import com.zeki.demo.exerciseLibrary.entity.ExerciseVideoEntity;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

@Component
public class WgerExerciseClient {

    private static final int ENGLISH_LANGUAGE_ID = 2;

    private final WebClient webClient;

    public WgerExerciseClient(
            WebClient.Builder builder,
            @Value("${wger.base-url:https://wger.de/api/v2}") String baseUrl
    ) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public ExerciseLibraryEntity searchExercises(ExerciseLibrarySearchCriteria criteria) {
        try {
            WgerExerciseListResponse response = webClient.get()
                    .uri(uriBuilder -> exerciseListUri(uriBuilder, criteria))
                    .retrieve()
                    .bodyToMono(WgerExerciseListResponse.class)
                    .block();

            List<ExerciseLibraryItemEntity> results = response != null && response.results() != null
                    ? filterByQuery(response.results(), criteria.q()).stream()
                            .filter(WgerExercise::hasEnglishTranslation)
                            .map(WgerExercise::toExerciseLibraryItemEntity)
                            .toList()
                    : List.of();

            return new ExerciseLibraryEntity(results, results.size(), response != null ? response.count() : 0);
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Exercise search failed");
        }
    }

    public ExerciseLibraryItemEntity getExerciseById(Integer exerciseId) {
        try {
            WgerExercise response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/exerciseinfo/{exerciseId}/")
                            .queryParam("language", ENGLISH_LANGUAGE_ID)
                            .build(exerciseId))
                    .retrieve()
                    .bodyToMono(WgerExercise.class)
                    .block();
            if (response == null) {
                throw new NotFoundException("Exercise not found");
            }
            if (!response.hasEnglishTranslation()) {
                throw new NotFoundException("English exercise translation not found");
            }
            return response.toExerciseLibraryItemEntity();
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Exercise not found");
        }
    }

    public List<ExerciseMuscleEntity> getMuscles(Integer limit, Integer offset) {
        try {
            WgerMuscleListResponse response = webClient.get()
                    .uri(uriBuilder -> referenceListUri(uriBuilder, "/muscle/", limit, offset))
                    .retrieve()
                    .bodyToMono(WgerMuscleListResponse.class)
                    .block();
            return response != null && response.results() != null
                    ? response.results().stream().map(WgerMuscle::toExerciseMuscleEntity).toList()
                    : List.of();
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Exercise muscles could not be loaded");
        }
    }

    public List<ExerciseReferenceEntity> getEquipment(Integer limit, Integer offset) {
        return getReferences("/equipment/", limit, offset, "Exercise equipment could not be loaded");
    }

    public List<ExerciseReferenceEntity> getCategories(Integer limit, Integer offset) {
        return getReferences("/exercisecategory/", limit, offset, "Exercise categories could not be loaded");
    }

    private List<ExerciseReferenceEntity> getReferences(String path, Integer limit, Integer offset, String errorMessage) {
        try {
            WgerReferenceListResponse response = webClient.get()
                    .uri(uriBuilder -> referenceListUri(uriBuilder, path, limit, offset))
                    .retrieve()
                    .bodyToMono(WgerReferenceListResponse.class)
                    .block();
            return response != null && response.results() != null
                    ? response.results().stream().map(WgerReference::toExerciseReferenceEntity).toList()
                    : List.of();
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, errorMessage);
        }
    }

    private URI exerciseListUri(UriBuilder uriBuilder, ExerciseLibrarySearchCriteria criteria) {
        UriBuilder builder = uriBuilder.path("/exerciseinfo/");
        addIfPresent(builder, "limit", criteria.limit() != null ? criteria.limit() : 20);
        addIfPresent(builder, "offset", criteria.offset());
        addIfPresent(builder, "language", ENGLISH_LANGUAGE_ID);
        addIfPresent(builder, "muscles", criteria.muscleId());
        addIfPresent(builder, "equipment", criteria.equipmentId());
        addIfPresent(builder, "category", criteria.categoryId());
        return builder.build();
    }

    private URI referenceListUri(UriBuilder uriBuilder, String path, Integer limit, Integer offset) {
        UriBuilder builder = uriBuilder.path(path);
        addIfPresent(builder, "limit", limit != null ? limit : 100);
        addIfPresent(builder, "offset", offset);
        return builder.build();
    }

    private List<WgerExercise> filterByQuery(List<WgerExercise> exercises, String query) {
        if (query == null || query.isBlank()) {
            return exercises;
        }
        String normalizedQuery = query.trim().toLowerCase();
        return exercises.stream()
                .filter(exercise -> exercise.matches(normalizedQuery))
                .toList();
    }

    private void addIfPresent(UriBuilder builder, String name, Object value) {
        if (value != null) {
            builder.queryParam(name, value);
        }
    }

    private RuntimeException mapExternalError(WebClientResponseException ex, String defaultMessage) {
        if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            return new NotFoundException(defaultMessage);
        }
        if (ex.getStatusCode().is4xxClientError()) {
            return new BadRequestException(defaultMessage);
        }
        return new ExternalServiceException("wger exercise service is unavailable");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerExerciseListResponse(
            Integer count,
            List<WgerExercise> results
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerReferenceListResponse(
            List<WgerReference> results
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerMuscleListResponse(
            List<WgerMuscle> results
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerExercise(
            Integer id,
            String uuid,
            WgerReference category,
            List<WgerMuscle> muscles,
            @JsonProperty("muscles_secondary")
            List<WgerMuscle> musclesSecondary,
            List<WgerReference> equipment,
            WgerLicense license,
            @JsonProperty("license_author")
            String licenseAuthor,
            List<WgerImage> images,
            List<WgerTranslation> translations,
            List<WgerVideo> videos
    ) {
        ExerciseLibraryItemEntity toExerciseLibraryItemEntity() {
            WgerTranslation translation = englishTranslation();
            return new ExerciseLibraryItemEntity(
                    id,
                    uuid,
                    translation != null ? translation.name() : null,
                    translation != null ? translation.description() : null,
                    translation != null ? translation.descriptionSource() : null,
                    category != null ? category.toExerciseReferenceEntity() : null,
                    muscles != null ? muscles.stream().map(WgerMuscle::toExerciseMuscleEntity).toList() : List.of(),
                    musclesSecondary != null
                            ? musclesSecondary.stream().map(WgerMuscle::toExerciseMuscleEntity).toList()
                            : List.of(),
                    equipment != null ? equipment.stream().map(WgerReference::toExerciseReferenceEntity).toList() : List.of(),
                    translation != null ? translation.aliasValues() : List.of(),
                    images != null ? images.stream().map(WgerImage::toExerciseImageEntity).toList() : List.of(),
                    videos != null ? videos.stream().map(WgerVideo::toExerciseVideoEntity).toList() : List.of(),
                    license != null ? license.toExerciseLicenseEntity() : null,
                    licenseAuthor
            );
        }

        boolean matches(String query) {
            WgerTranslation translation = englishTranslation();
            if (translation == null) {
                return false;
            }
            return contains(translation.name(), query)
                    || contains(translation.descriptionSource(), query)
                    || translation.aliasValues().stream().anyMatch(alias -> contains(alias, query));
        }

        boolean hasEnglishTranslation() {
            return englishTranslation() != null;
        }

        private WgerTranslation englishTranslation() {
            return translations != null
                    ? translations.stream()
                            .filter(translation -> translation.language() != null
                                    && translation.language() == ENGLISH_LANGUAGE_ID)
                            .findFirst()
                            .orElse(null)
                    : null;
        }

        private boolean contains(String value, String query) {
            return value != null && value.toLowerCase().contains(query);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerTranslation(
            String name,
            String description,
            @JsonProperty("description_source")
            String descriptionSource,
            Integer language,
            List<WgerAlias> aliases
    ) {
        List<String> aliasValues() {
            return aliases != null ? aliases.stream().map(WgerAlias::alias).toList() : List.of();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerAlias(String alias) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerReference(Integer id, String name) {
        ExerciseReferenceEntity toExerciseReferenceEntity() {
            return new ExerciseReferenceEntity(id, name);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerMuscle(
            Integer id,
            String name,
            @JsonProperty("name_en")
            String nameEn,
            @JsonProperty("is_front")
            Boolean isFront,
            @JsonProperty("image_url_main")
            String imageUrlMain,
            @JsonProperty("image_url_secondary")
            String imageUrlSecondary
    ) {
        ExerciseMuscleEntity toExerciseMuscleEntity() {
            return new ExerciseMuscleEntity(id, name, nameEn, isFront, imageUrlMain, imageUrlSecondary);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerImage(
            Integer id,
            String image,
            @JsonProperty("is_main")
            Boolean isMain
    ) {
        ExerciseImageEntity toExerciseImageEntity() {
            return new ExerciseImageEntity(id, image, isMain);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerVideo(
            Integer id,
            String video,
            @JsonProperty("is_main")
            Boolean isMain,
            String duration
    ) {
        ExerciseVideoEntity toExerciseVideoEntity() {
            return new ExerciseVideoEntity(id, video, isMain, duration);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerLicense(
            Integer id,
            @JsonProperty("full_name")
            String fullName,
            @JsonProperty("short_name")
            String shortName,
            String url
    ) {
        ExerciseLicenseEntity toExerciseLicenseEntity() {
            return new ExerciseLicenseEntity(id, fullName, shortName, url);
        }
    }
}
