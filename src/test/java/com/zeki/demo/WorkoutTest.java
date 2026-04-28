package com.zeki.demo;

import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.workouts.mapperDtoDomain.WorkoutDtoDomain;
import com.zeki.demo.workouts.repository.WorkoutRepository;
import com.zeki.demo.workouts.service.WorkoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkoutTest {

    @Mock
    private WorkoutDtoDomain workoutDtoDomain;
    @Mock
    private WorkoutRepository workoutRepository;
    private WorkoutServiceImpl workoutService;

    //no need for new the mock object.In this case only service interface is normal
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        workoutService = new WorkoutServiceImpl( workoutRepository, workoutDtoDomain);
    }

    @Test
    void createWorkoutReturnsCreatedWorkoutName() {
        Workout createWorkout = new CreateWorkout(
                "Leg Day",
                LocalDate.of(2026, 5, 16),
                60,
                "a"

        );

        UUID workoutId = UUID.randomUUID();

        WorkoutResponse response = new WorkoutResponse(
                workoutId,
                LocalDate.of(2026, 5, 16),
                60,
                "Leg Day",
                "a",
                null,

        );

        Mockito.when(workoutRepository.existsByUserEmailAndWorkoutDate("test@example.com", request.getWorkoutDate(), request.getWorkoutName())).thenReturn(false);
        Mockito.when(workoutDtoDomain.toDto(Mockito.any())).thenReturn(response);

        WorkoutResponse actual = workoutService.createWorkout("test@example.com", );

        assertEquals("Leg Day", actual.getWorkoutName());

    }

    @Test
    void getMyWorkoutsReturnsWorkoutList() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("test@example.com", null);
        List<WorkoutResponse> response = List.of(
                new WorkoutResponse(
                        UUID.randomUUID(),
                        LocalDate.of(2026, 5, 16),
                        60,
                        "Leg Day",
                        "Heavy squats",
                        null,
                        "General",
                        Boolean.FALSE,
                        LocalDate.of(2026, 4, 23),
                        LocalDate.of(2026, 4, 23)
                ),
                new WorkoutResponse(
                        UUID.randomUUID(),
                        LocalDate.of(2026, 5, 17),
                        30,
                        "Run",
                        "Easy pace",
                        null,
                        "General",
                        Boolean.FALSE,
                        LocalDate.of(2026, 4, 23),
                        LocalDate.of(2026, 4, 23)
                )
        );

        Mockito.when(workoutService.getMyWorkouts("test@example.com")).thenReturn(response);

        List<WorkoutResponse> actual = workoutController.getMyWorkouts(authentication);

        assertEquals(2, actual.size());
        Mockito.verify(workoutService).getMyWorkouts("test@example.com");
    }
}
