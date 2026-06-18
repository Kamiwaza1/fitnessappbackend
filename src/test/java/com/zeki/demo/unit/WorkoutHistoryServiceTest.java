package com.zeki.demo.Unit;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import com.zeki.demo.WorkoutHistory.dto.CreateWorkoutHistoryRequest;
import com.zeki.demo.WorkoutHistory.mapperDtoDomain.WorkoutHistoryDtoDomain;
import com.zeki.demo.WorkoutHistory.repository.WorkoutHistoryRepository;
import com.zeki.demo.WorkoutHistory.service.WorkoutHistoryServiceImpl;
import com.zeki.demo.workouts.WorkoutException.WorkoutExistException;
import com.zeki.demo.workouts.repository.WorkoutRepository;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutHistoryServiceTest {

    @Mock
    private WorkoutHistoryRepository workoutHistoryRepository;

    @Mock
    private WorkoutHistoryDtoDomain workoutHistoryDtoDomain;

    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private WorkoutHistoryServiceImpl workoutHistoryService;

    @Test
    void getWorkoutHistoryDelegatesToRepository() {
        List<WorkoutHistory> history = List.of(history(UUID.randomUUID()));
        when(workoutHistoryRepository.findAllByUserEmail("user@example.com")).thenReturn(history);

        List<WorkoutHistory> result = workoutHistoryService.getWorkoutHistory("user@example.com");

        assertEquals(history, result);
    }

    @Test
    void createWorkoutHistoryMapsAndSavesRequest() {
        UUID workoutId = UUID.randomUUID();
        CreateWorkoutHistoryRequest request = new CreateWorkoutHistoryRequest(
                workoutId,
                "Completed Workout",
                LocalDate.now(),
                null,
                3,
                10,
                20.0,
                null,
                null,
                null,
                null,
                null,
                List.of()
        );
        WorkoutHistory history = history(workoutId);

        when(workoutRepository.getWorkoutByIdAndUserEmail(workoutId, "user@example.com")).thenReturn(Optional.empty());
        when(workoutHistoryDtoDomain.toDomain(request, "user@example.com", null)).thenReturn(history);
        when(workoutHistoryRepository.save(history)).thenReturn(history);

        WorkoutHistory result = workoutHistoryService.createWorkoutHistory(request, "user@example.com");

        assertEquals(history, result);
    }

    @Test
    void getWorkoutHistoryByIdWhenMissingThrowsNotFound() {
        UUID workoutId = UUID.randomUUID();
        when(workoutHistoryRepository.getByWorkoutIdAndUserEmail(workoutId, "user@example.com"))
                .thenReturn(Optional.empty());

        WorkoutExistException ex = assertThrows(
                WorkoutExistException.class,
                () -> workoutHistoryService.getWorkoutHistoryById(workoutId, "user@example.com")
        );

        assertEquals("Workout not found", ex.getMessage());
    }

    @Test
    void getWorkoutHistoryByIdReturnsHistoryWhenFound() {
        UUID workoutId = UUID.randomUUID();
        WorkoutHistory history = history(workoutId);
        when(workoutHistoryRepository.getByWorkoutIdAndUserEmail(workoutId, "user@example.com"))
                .thenReturn(Optional.of(history));

        WorkoutHistory result = workoutHistoryService.getWorkoutHistoryById(workoutId, "user@example.com");

        assertEquals(history, result);
    }

    private WorkoutHistory history(UUID workoutId) {
        LocalDate now = LocalDate.now();
        return new WorkoutHistory(
                workoutId,
                now,
                60,
                4,
                8,
                90.0,
                "Leg Day",
                "notes",
                "description",
                WorkoutCategory.STRENGTH,
                MuscleType.LEGS,
                now,
                now.minusDays(1),
                "user@example.com"
        );
    }
}
