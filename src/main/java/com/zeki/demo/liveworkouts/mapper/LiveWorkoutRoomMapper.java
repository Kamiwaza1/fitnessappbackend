package com.zeki.demo.liveworkouts.mapper;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutParticipant;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutParticipantResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutRoomResponse;
import org.springframework.stereotype.Component;

@Component
public class LiveWorkoutRoomMapper {

    public LiveWorkoutRoomResponse toResponse(LiveWorkoutRoom room) {
        return new LiveWorkoutRoomResponse(
                room.roomCode(),
                room.hostEmail(),
                room.workoutId(),
                room.workoutName(),
                room.participants().values().stream().map(this::toResponse).toList(),
                room.createdAt(),
                room.lastActivityAt()
        );
    }

    private LiveWorkoutParticipantResponse toResponse(LiveWorkoutParticipant participant) {
        return new LiveWorkoutParticipantResponse(
                participant.email(),
                participant.displayName(),
                participant.status(),
                participant.currentExerciseIndex(),
                participant.completedSets(),
                participant.joinedAt()
        );
    }
}
