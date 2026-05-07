package com.zeki.demo.Unit;

import com.zeki.demo.progress.domain.Progress;
import com.zeki.demo.progress.dto.ProgressResponse;
import com.zeki.demo.progress.mapperDtoDomain.ProgressDtoDomain;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.mapperDomainEntity.UserDomainEntity;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.domain.WorkoutExercise;
import com.zeki.demo.workouts.entity.WorkoutEntity;
import com.zeki.demo.workouts.entity.WorkoutExerciseValue;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.mapperDtoDomain.WorkoutDtoDomain;
import com.zeki.demo.workouts.mapperEntityDomain.WorkoutEntityMapper;
import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.entity.NutritionEntity;
import com.zeki.demo.nutrition.enums.NutritionCategory;
import com.zeki.demo.nutrition.mapperEntityDomain.NutritionEntityMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapperCoverageTest {

    private final WorkoutEntityMapper workoutEntityMapper = new WorkoutEntityMapper();
    private final NutritionEntityMapper nutritionEntityMapper = new NutritionEntityMapper();
    private final UserDomainEntity userDomainEntity = new UserDomainEntity();
    private final ProgressDtoDomain progressDtoDomain = new ProgressDtoDomain(new WorkoutDtoDomain());

    @Test
    void workoutEntityMapperMapsDomainAndEntityBothWays() {
        UserEntity userEntity = userEntity();
        Workout workout = workoutWithExercises();

        WorkoutEntity entity = workoutEntityMapper.toEntity(workout, userEntity);

        assertEquals(workout.getWorkoutName(), entity.getWorkoutName());
        assertEquals(2, entity.getExercises().size());
        assertEquals(userEntity, entity.getUser());
        assertEquals(workout.getUserEmail(), entity.getUserEmail());

        Workout mappedBack = workoutEntityMapper.toDomain(entity);

        assertEquals(workout.getWorkoutName(), mappedBack.getWorkoutName());
        assertEquals(workout.getCategory(), mappedBack.getCategory());
        assertEquals(2, mappedBack.getExercises().size());
        assertEquals("Squat", mappedBack.getExercises().get(0).getName());
    }

    @Test
    void workoutEntityMapperReturnsEmptyExercisesWhenEntityExercisesAreNull() {
        WorkoutEntity entity = WorkoutEntity.builder()
                .workoutId(UUID.randomUUID())
                .workoutName("Simple")
                .description("desc")
                .category(WorkoutCategory.GENERAL)
                .muscleType(MuscleType.FULL_BODY)
                .createdAt(LocalDate.now().minusDays(2))
                .updatedAt(LocalDate.now().minusDays(1))
                .userEmail("user@example.com")
                .workoutDate(LocalDate.now().plusDays(1))
                .durationMinutes(30)
                .sets(3)
                .reps(10)
                .weightKg(20.0)
                .notes("note")
                .exercises(null)
                .build();

        Workout mapped = workoutEntityMapper.toDomain(entity);

        assertTrue(mapped.getExercises().isEmpty());
    }

    @Test
    void nutritionEntityMapperMapsDomainAndEntityBothWays() {
        UserEntity userEntity = userEntity();
        Nutrition nutrition = nutrition();

        NutritionEntity entity = nutritionEntityMapper.toEntity(nutrition, userEntity);

        assertEquals(nutrition.getMealName(), entity.getMealName());
        assertEquals(userEntity, entity.getUser());
        assertEquals(nutrition.getCategory(), entity.getCategory());

        Nutrition mappedBack = nutritionEntityMapper.toDomain(entity);

        assertEquals(nutrition.getMealName(), mappedBack.getMealName());
        assertEquals(nutrition.getCalories(), mappedBack.getCalories());
        assertEquals(nutrition.getUserEmail(), mappedBack.getUserEmail());
    }

    @Test
    void userDomainEntityMapsBothWaysAndFillsMissingTimestamps() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(2);
        LocalDateTime updatedAt = LocalDateTime.now().minusDays(1);
        UserEntity entity = UserEntity.builder()
                .userId(UUID.randomUUID())
                .fullName("  Test User  ")
                .email("USER@example.com")
                .password("encoded-password")
                .role("USER")
                .isActive(true)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        User domain = userDomainEntity.toDomain(entity);

        assertEquals("user@example.com", domain.getEmail());
        assertEquals(createdAt, domain.getCreatedAt());

        User userWithoutDates = new User(
                UUID.randomUUID(),
                null,
                "mapped@example.com",
                "EncodedPassword123!",
                "USER",
                true,
                null,
                null
        );

        UserEntity mappedEntity = userDomainEntity.toEntity(userWithoutDates);

        assertEquals("mapped", mappedEntity.getFullName());
        assertNotNull(mappedEntity.getCreatedAt());
        assertNotNull(mappedEntity.getUpdatedAt());
    }

    @Test
    void progressDtoDomainMapsNestedCollectionsAndNullPersonalRecord() {
        Workout workout = workoutWithExercises();
        Progress progress = new Progress(
                LocalDate.now().minusDays(3),
                LocalDate.now().plusDays(3),
                4500.0,
                90,
                2,
                List.of(workout),
                List.of(new Progress.WeeklyVolumePoint("May 1", 1200.0)),
                List.of(new Progress.CategoryVolumePoint(WorkoutCategory.STRENGTH, 4500.0)),
                new Progress.PersonalRecord("Leg Day", 140.0, 5, 5, 3500.0, "2026-05-01")
        );

        ProgressResponse response = progressDtoDomain.toResponse(progress);

        assertEquals(1, response.recentActivity().size());
        assertEquals(2, response.recentActivity().get(0).exercises().size());
        assertEquals("May 1", response.weeklyVolumeTrend().get(0).weekLabel());
        assertEquals(WorkoutCategory.STRENGTH, response.volumeByCategory().get(0).category());
        assertEquals("Leg Day", response.personalRecord().workoutName());

        ProgressResponse withoutPr = progressDtoDomain.toResponse(new Progress(
                progress.weekStartDate(),
                progress.weekEndDate(),
                progress.weeklyTrainingVolumeKg(),
                progress.weeklyDurationMinutes(),
                progress.weeklyWorkoutsCount(),
                progress.recentActivity(),
                progress.weeklyVolumeTrend(),
                progress.volumeByCategory(),
                null
        ));

        assertNull(withoutPr.personalRecord());
    }

    private Workout workoutWithExercises() {
        LocalDate date = LocalDate.now().plusDays(1);
        return new Workout(
                "Leg Day",
                date,
                60,
                7,
                17,
                100.0,
                "notes",
                UUID.randomUUID(),
                "description",
                WorkoutCategory.STRENGTH,
                MuscleType.LEGS,
                List.of(
                        new WorkoutExercise("Squat", MuscleType.LEGS, 4, 8, 100.0, "heavy"),
                        new WorkoutExercise("Lunge", MuscleType.LEGS, 3, 9, 40.0, "controlled")
                ),
                date.minusDays(2),
                date.minusDays(1),
                "user@example.com"
        );
    }

    private Nutrition nutrition() {
        LocalDate now = LocalDate.now();
        return new Nutrition(
                UUID.randomUUID(),
                "Chicken Bowl",
                now,
                650,
                40,
                50,
                20,
                "post workout",
                NutritionCategory.LUNCH,
                now.minusDays(2),
                now.minusDays(1),
                "user@example.com"
        );
    }

    private UserEntity userEntity() {
        return UserEntity.builder()
                .userId(UUID.randomUUID())
                .fullName("Test User")
                .email("user@example.com")
                .password("encoded-password")
                .role("USER")
                .isActive(true)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }
}
