package com.zeki.demo.workouts.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteWorkoutRequest {

  @NotNull
  private UUID workoutId;

  public DeleteWorkoutRequest(UUID workoutId) {
    this.workoutId = workoutId;
  }

}
