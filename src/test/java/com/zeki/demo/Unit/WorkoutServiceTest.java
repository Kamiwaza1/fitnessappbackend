package com.zeki.demo.Unit;

import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.workouts.WorkoutException.WorkoutExistException;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.mapperDtoDomain.WorkoutDtoDomain;
import com.zeki.demo.workouts.repository.WorkoutRepository;
import com.zeki.demo.workouts.service.WorkoutServiceImpl;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    private static final String OWNER_EMAIL = "owner@example.com";
    private static final String OTHER_EMAIL = "other@example.com";
    private static final String UPDATED_NAME = "Updated name";
    private static final String UPDATED_NOTES = "Updated notes";
    private static final String UPDATED_DESCRIPTION = "Updated description";

    @Mock
    private WorkoutRepository workoutRepository;

    private WorkoutServiceImpl workoutService;

    @BeforeEach
    void setUp() {
        workoutService = new WorkoutServiceImpl(workoutRepository, new WorkoutDtoDomain());
    }

    @Test
    void getWorkoutByIdWhenMissingThrowsNotFound() {
        UUID workoutId = UUID.randomUUID();
        when(workoutRepository.getWorkoutById(workoutId)).thenReturn(Optional.empty());

        WorkoutExistException ex = assertThrows(WorkoutExistException.class, () -> workoutService.getWorkoutById(workoutId));

        assertEquals("Workout not found", ex.getMessage());
    }

    @Test
    void updateWorkoutWhenNotOwnedByUserThrowsUnauthorized() {
        UUID workoutId = UUID.randomUUID();
        Workout existing = validWorkout(workoutId, OWNER_EMAIL);
        UpdateWorkoutRequest request = updateRequest(
                workoutId,
                LocalDate.now().plusDays(1),
                45,
                5,
                12,
                85.0,
                MuscleType.CORE
        );

        when(workoutRepository.getWorkoutById(workoutId)).thenReturn(Optional.of(existing));
        when(workoutRepository.isOwnedByUser(workoutId, OTHER_EMAIL)).thenReturn(false);

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> workoutService.updateWorkout(workoutId, request, OTHER_EMAIL)
        );

        assertEquals("You cannot update this workout", ex.getMessage());
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void updateWorkoutWhenOwnedPersistsUpdatedWorkout() {
        UUID workoutId = UUID.randomUUID();
        Workout existing = validWorkout(workoutId, OWNER_EMAIL);
        UpdateWorkoutRequest request = updateRequest(
                workoutId,
                LocalDate.now().plusDays(2),
                50,
                6,
                6,
                140.0,
                MuscleType.CHEST
        );

        when(workoutRepository.getWorkoutById(workoutId)).thenReturn(Optional.of(existing));
        when(workoutRepository.isOwnedByUser(workoutId, OWNER_EMAIL)).thenReturn(true);
        when(workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Workout updated = workoutService.updateWorkout(workoutId, request, OWNER_EMAIL);

        assertEquals(UPDATED_NAME, updated.getWorkoutName());
        assertEquals(WorkoutCategory.CARDIO, updated.getCategory());
        assertEquals(50, updated.getDurationMinutes());
        assertEquals(6, updated.getSets());
        assertEquals(6, updated.getReps());
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    void deleteWorkoutWhenFoundDeletesById() {
        UUID workoutId = UUID.randomUUID();
        Workout existing = validWorkout(workoutId, OWNER_EMAIL);
        when(workoutRepository.getWorkoutById(workoutId)).thenReturn(Optional.of(existing));

        workoutService.deleteWorkout(workoutId);

        verify(workoutRepository).delete(eq(workoutId));
    }

    private Workout validWorkout(UUID workoutId, String userEmail) {
        LocalDate now = LocalDate.now();
        return new Workout(
                "Workout name",
                now.plusDays(1),
                30,
                4,
                10,
                80.0,
                "notes",
                workoutId,
                "description",
                WorkoutCategory.GENERAL,
                MuscleType.FULL_BODY,
                null,
                now,
                now,
                userEmail
        );
    }

    private UpdateWorkoutRequest updateRequest(
            UUID workoutId,
            LocalDate workoutDate,
            Integer durationMinutes,
            Integer sets,
            Integer reps,
            Double weightKg,
            MuscleType muscleType
    ) {
        return new UpdateWorkoutRequest(
                workoutId,
                UPDATED_NAME,
                workoutDate,
                durationMinutes,
                sets,
                reps,
                weightKg,
                UPDATED_NOTES,
                WorkoutCategory.CARDIO,
                muscleType,
                null,
                UPDATED_DESCRIPTION
        );
    }
}
