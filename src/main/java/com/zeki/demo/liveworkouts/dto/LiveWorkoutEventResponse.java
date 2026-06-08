package com.zeki.demo.liveworkouts.dto;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutEventType;

public record LiveWorkoutEventResponse(
        LiveWorkoutEventType type,
        String actorEmail,
        LiveWorkoutRoomResponse room,
        LiveWorkoutMessageResponse message
) {
    public LiveWorkoutEventResponse(LiveWorkoutEventType type, String actorEmail, LiveWorkoutRoomResponse room) {
        this(type, actorEmail, room, null);
    }
}
