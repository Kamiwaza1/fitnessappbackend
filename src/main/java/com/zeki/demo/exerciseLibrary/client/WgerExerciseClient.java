package com.zeki.demo.exerciseLibrary.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.ExternalServiceException;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.exerciseLibrary.dto.ExerciseImageResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryItemResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibrarySearchRequest;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLicenseResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseMuscleResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseReferenceResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseVideoResponse;
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

    private final WebClient webClient;

    public WgerExerciseClient(
            WebClient.Builder builder,
            @Value("${wger.base-url:https://wger.de/api/v2}") String baseUrl
    ) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public ExerciseLibraryResponse searchExercises(ExerciseLibrarySearchRequest request) {
        try {
            WgerExerciseListResponse response = webClient.get()
                    .uri(uriBuilder -> exerciseListUri(uriBuilder, request))
                    .retrieve()
                    .bodyToMono(WgerExerciseListResponse.class)
                    .block();

            List<ExerciseLibraryItemResponse> results = response != null && response.results() != null
                    ? filterByQuery(response.results(), request.q()).stream()
                            .map(WgerExercise::toExerciseLibraryItemResponse)
                            .toList()
                    : List.of();

            return new ExerciseLibraryResponse(results, results.size(), response != null ? response.count() : 0);
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Exercise search failed");
        }
    }

    public ExerciseLibraryItemResponse getExerciseById(Integer exerciseId) {
        try {
            WgerExercise response = webClient.get()
                    .uri("/exerciseinfo/{exerciseId}/", exerciseId)
                    .retrieve()
                    .bodyToMono(WgerExercise.class)
                    .block();
            if (response == null) {
                throw new NotFoundException("Exercise not found");
            }
            return response.toExerciseLibraryItemResponse();
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Exercise not found");
        }
    }

    public List<ExerciseMuscleResponse> getMuscles(Integer limit, Integer offset) {
        try {
            WgerMuscleListResponse response = webClient.get()
                    .uri(uriBuilder -> referenceListUri(uriBuilder, "/muscle/", limit, offset))
                    .retrieve()
                    .bodyToMono(WgerMuscleListResponse.class)
                    .block();
            return response != null && response.results() != null
                    ? response.results().stream().map(WgerMuscle::toExerciseMuscleResponse).toList()
                    : List.of();
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, "Exercise muscles could not be loaded");
        }
    }

    public List<ExerciseReferenceResponse> getEquipment(Integer limit, Integer offset) {
        return getReferences("/equipment/", limit, offset, "Exercise equipment could not be loaded");
    }

    public List<ExerciseReferenceResponse> getCategories(Integer limit, Integer offset) {
        return getReferences("/exercisecategory/", limit, offset, "Exercise categories could not be loaded");
    }

    private List<ExerciseReferenceResponse> getReferences(String path, Integer limit, Integer offset, String errorMessage) {
        try {
            WgerReferenceListResponse response = webClient.get()
                    .uri(uriBuilder -> referenceListUri(uriBuilder, path, limit, offset))
                    .retrieve()
                    .bodyToMono(WgerReferenceListResponse.class)
                    .block();
            return response != null && response.results() != null
                    ? response.results().stream().map(WgerReference::toExerciseReferenceResponse).toList()
                    : List.of();
        } catch (WebClientResponseException ex) {
            throw mapExternalError(ex, errorMessage);
        }
    }

    private URI exerciseListUri(UriBuilder uriBuilder, ExerciseLibrarySearchRequest request) {
        UriBuilder builder = uriBuilder.path("/exerciseinfo/");
        addIfPresent(builder, "limit", request.limit() != null ? request.limit() : 20);
        addIfPresent(builder, "offset", request.offset());
        addIfPresent(builder, "language", request.language() != null ? request.language() : 2);
        addIfPresent(builder, "muscles", request.muscleId());
        addIfPresent(builder, "equipment", request.equipmentId());
        addIfPresent(builder, "category", request.categoryId());
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
        ExerciseLibraryItemResponse toExerciseLibraryItemResponse() {
            WgerTranslation translation = firstTranslation();
            return new ExerciseLibraryItemResponse(
                    id,
                    uuid,
                    translation != null ? translation.name() : null,
                    translation != null ? translation.description() : null,
                    translation != null ? translation.descriptionSource() : null,
                    category != null ? category.toExerciseReferenceResponse() : null,
                    muscles != null ? muscles.stream().map(WgerMuscle::toExerciseMuscleResponse).toList() : List.of(),
                    musclesSecondary != null
                            ? musclesSecondary.stream().map(WgerMuscle::toExerciseMuscleResponse).toList()
                            : List.of(),
                    equipment != null ? equipment.stream().map(WgerReference::toExerciseReferenceResponse).toList() : List.of(),
                    translation != null ? translation.aliasValues() : List.of(),
                    images != null ? images.stream().map(WgerImage::toExerciseImageResponse).toList() : List.of(),
                    videos != null ? videos.stream().map(WgerVideo::toExerciseVideoResponse).toList() : List.of(),
                    license != null ? license.toExerciseLicenseResponse() : null,
                    licenseAuthor
            );
        }

        boolean matches(String query) {
            WgerTranslation translation = firstTranslation();
            if (translation == null) {
                return false;
            }
            return contains(translation.name(), query)
                    || contains(translation.descriptionSource(), query)
                    || translation.aliasValues().stream().anyMatch(alias -> contains(alias, query));
        }

        private WgerTranslation firstTranslation() {
            return translations != null && !translations.isEmpty() ? translations.get(0) : null;
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
        ExerciseReferenceResponse toExerciseReferenceResponse() {
            return new ExerciseReferenceResponse(id, name);
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
        ExerciseMuscleResponse toExerciseMuscleResponse() {
            return new ExerciseMuscleResponse(id, name, nameEn, isFront, imageUrlMain, imageUrlSecondary);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WgerImage(
            Integer id,
            String image,
            @JsonProperty("is_main")
            Boolean isMain
    ) {
        ExerciseImageResponse toExerciseImageResponse() {
            return new ExerciseImageResponse(id, image, isMain);
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
        ExerciseVideoResponse toExerciseVideoResponse() {
            return new ExerciseVideoResponse(id, video, isMain, duration);
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
        ExerciseLicenseResponse toExerciseLicenseResponse() {
            return new ExerciseLicenseResponse(id, fullName, shortName, url);
        }
    }
}
