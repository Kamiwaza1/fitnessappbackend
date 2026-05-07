package com.zeki.demo.Unit;

import com.zeki.demo.progress.domain.Progress;
import com.zeki.demo.progress.repository.ProgressRepository;
import com.zeki.demo.progress.service.ProgressServiceImpl;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.domain.WorkoutExercise;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private ProgressRepository progressRepository;

    @InjectMocks
    private ProgressServiceImpl progressService;

    @Test
    void getProgressAggregatesWeeklyTrendCategoryBreakdownAndPersonalRecord() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(WeekFields.ISO.dayOfWeek(), 1);

        Workout currentWeekStrength = workout(
                "Bench Day",
                weekStart.plusDays(1),
                60,
                4,
                8,
                100.0,
                WorkoutCategory.STRENGTH,
                List.of()
        );
        Workout currentWeekCardio = workout(
                "Circuit",
                weekStart.plusDays(2),
                45,
                3,
                12,
                20.0,
                WorkoutCategory.CARDIO,
                List.of(
                        new WorkoutExercise("Row", MuscleType.BACK, 3, 10, 30.0, "steady"),
                        new WorkoutExercise("Push", MuscleType.CHEST, 2, 12, 15.0, "fast")
                )
        );
        Workout threeWeeksAgo = workout(
                "Leg Day",
                weekStart.minusWeeks(3).plusDays(2),
                50,
                5,
                5,
                120.0,
                WorkoutCategory.STRENGTH,
                List.of()
        );
        Workout futureWorkout = workout(
                "Future Session",
                weekStart.plusDays(7),
                40,
                2,
                20,
                10.0,
                WorkoutCategory.GENERAL,
                List.of()
        );

        when(progressRepository.findAllByUserEmail("user@example.com"))
                .thenReturn(List.of(futureWorkout, currentWeekCardio, currentWeekStrength, threeWeeksAgo));

        Progress progress = progressService.getProgress("user@example.com");

        assertEquals(4460.0, progress.weeklyTrainingVolumeKg());
        assertEquals(105, progress.weeklyDurationMinutes());
        assertEquals(2, progress.weeklyWorkoutsCount());
        assertEquals(List.of("Circuit", "Bench Day", "Leg Day"),
                progress.recentActivity().stream().map(Workout::getWorkoutName).toList());

        assertEquals(4, progress.weeklyVolumeTrend().size());
        assertEquals(3000.0, progress.weeklyVolumeTrend().get(0).trainingVolumeKg());
        assertEquals(0.0, progress.weeklyVolumeTrend().get(1).trainingVolumeKg());
        assertEquals(0.0, progress.weeklyVolumeTrend().get(2).trainingVolumeKg());
        assertEquals(4460.0, progress.weeklyVolumeTrend().get(3).trainingVolumeKg());

        assertEquals(3, progress.volumeByCategory().size());
        assertEquals(WorkoutCategory.STRENGTH, progress.volumeByCategory().get(0).category());
        assertEquals(6200.0, progress.volumeByCategory().get(0).trainingVolumeKg());
        assertEquals(WorkoutCategory.CARDIO, progress.volumeByCategory().get(1).category());
        assertEquals(1260.0, progress.volumeByCategory().get(1).trainingVolumeKg());
        assertEquals(WorkoutCategory.GENERAL, progress.volumeByCategory().get(2).category());
        assertEquals(400.0, progress.volumeByCategory().get(2).trainingVolumeKg());

        assertNotNull(progress.personalRecord());
        assertEquals("Bench Day", progress.personalRecord().workoutName());
        assertEquals(100.0, progress.personalRecord().weightKg());
        assertEquals(8, progress.personalRecord().reps());
        assertEquals(4, progress.personalRecord().sets());
        assertEquals(3200.0, progress.personalRecord().trainingVolumeKg());
        assertEquals(currentWeekStrength.getWorkoutDate().toString(), progress.personalRecord().workoutDate());
    }

    @Test
    void getProgressReturnsEmptySummariesWhenUserHasNoWorkouts() {
        when(progressRepository.findAllByUserEmail("empty@example.com")).thenReturn(List.of());

        Progress progress = progressService.getProgress("empty@example.com");

        assertEquals(0.0, progress.weeklyTrainingVolumeKg());
        assertEquals(0, progress.weeklyDurationMinutes());
        assertEquals(0, progress.weeklyWorkoutsCount());
        assertEquals(List.of(), progress.recentActivity());
        assertEquals(4, progress.weeklyVolumeTrend().size());
        assertEquals(List.of(), progress.volumeByCategory());
        assertNull(progress.personalRecord());
    }

    private Workout workout(
            String name,
            LocalDate workoutDate,
            int duration,
            int sets,
            int reps,
            double weightKg,
            WorkoutCategory category,
            List<WorkoutExercise> exercises
    ) {
        LocalDate createdAt = workoutDate.minusDays(2);
        return new Workout(
                name,
                workoutDate,
                duration,
                sets,
                reps,
                weightKg,
                "notes",
                UUID.randomUUID(),
                "description",
                category,
                MuscleType.FULL_BODY,
                exercises,
                createdAt,
                createdAt.plusDays(1),
                "user@example.com"
        );
    }
}
