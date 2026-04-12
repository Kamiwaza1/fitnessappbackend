package com.zeki.demo;

import com.zeki.demo.controller.WorkoutController;
import com.zeki.demo.dto.CreateWorkoutRequest;
import com.zeki.demo.dto.UpdateWorkoutRequest;
import com.zeki.demo.dto.WorkoutResponse;
import com.zeki.demo.service.Interface.IWorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class WorkoutControllerTest {

    @Mock
    private IWorkoutService workoutService;

    private WorkoutController workoutController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        workoutController = new WorkoutController(workoutService);
    }

    @Test
    void createWorkout_ValidRequest_ReturnsCreatedWorkout() {
        CreateWorkoutRequest request = new CreateWorkoutRequest();
        request.setName("Leg Day");
        request.setWorkoutDate(LocalDate.of(2026, 3, 16));
        request.setDurationMinutes(60);
        request.setNotes("Heavy squats and lunges");

        Authentication authentication = new UsernamePasswordAuthenticationToken("test@example.com", null);

        WorkoutResponse responseBody = new WorkoutResponse(
                1L,
                "Leg Day",
                LocalDate.of(2026, 3, 16),
                60,
                "Heavy squats and lunges"
        );

        Mockito.when(workoutService.createWorkout(Mockito.eq("test@example.com"), any(CreateWorkoutRequest.class)))
                .thenReturn(responseBody);

        ResponseEntity<WorkoutResponse> response = workoutController.createWorkout(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Leg Day", response.getBody().getName());
        Mockito.verify(workoutService).createWorkout("test@example.com", request);
    }

    @Test
    void getWorkouts_ReturnsWorkoutList() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("test@example.com", null);
        List<WorkoutResponse> responseBody = List.of(
                new WorkoutResponse(1L, "Leg Day", LocalDate.of(2026, 3, 16), 60, "Heavy squats"),
                new WorkoutResponse(2L, "Run", LocalDate.of(2026, 3, 15), 30, "Easy pace")
        );

        Mockito.when(workoutService.getWorkouts("test@example.com")).thenReturn(responseBody);

        ResponseEntity<List<WorkoutResponse>> response = workoutController.getWorkouts(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        Mockito.verify(workoutService).getWorkouts("test@example.com");
    }

    @Test
    void getWorkoutById_ReturnsWorkout() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("test@example.com", null);
        WorkoutResponse responseBody = new WorkoutResponse(1L, "Leg Day", LocalDate.of(2026, 3, 16), 60, "Heavy squats");

        Mockito.when(workoutService.getWorkoutById("test@example.com", 1L)).thenReturn(responseBody);

        ResponseEntity<WorkoutResponse> response = workoutController.getWorkoutById(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        Mockito.verify(workoutService).getWorkoutById("test@example.com", 1L);
    }

    @Test
    void updateWorkout_ValidRequest_ReturnsUpdatedWorkout() {
        UpdateWorkoutRequest request = new UpdateWorkoutRequest();
        request.setName("Push Day");
        request.setWorkoutDate(LocalDate.of(2026, 3, 17));
        request.setDurationMinutes(50);
        request.setNotes("Bench and dips");

        Authentication authentication = new UsernamePasswordAuthenticationToken("test@example.com", null);
        WorkoutResponse responseBody = new WorkoutResponse(1L, "Push Day", LocalDate.of(2026, 3, 17), 50, "Bench and dips");

        Mockito.when(workoutService.updateWorkout(Mockito.eq("test@example.com"), Mockito.eq(1L), any(UpdateWorkoutRequest.class)))
                .thenReturn(responseBody);

        ResponseEntity<WorkoutResponse> response = workoutController.updateWorkout(1L, request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Push Day", response.getBody().getName());
        Mockito.verify(workoutService).updateWorkout("test@example.com", 1L, request);
    }

    @Test
    void deleteWorkout_ReturnsNoContent() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("test@example.com", null);

        ResponseEntity<Void> response = workoutController.deleteWorkout(1L, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Mockito.verify(workoutService).deleteWorkout("test@example.com", 1L);
    }
}
