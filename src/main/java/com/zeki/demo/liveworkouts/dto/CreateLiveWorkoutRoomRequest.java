package com.zeki.demo.liveworkouts.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateLiveWorkoutRoomRequest(
        @NotNull(message = "Workout ID is required")
        UUID workoutId
) {
}
