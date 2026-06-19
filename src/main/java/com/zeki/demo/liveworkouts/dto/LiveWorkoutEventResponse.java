package com.zeki.demo.liveworkouts.dto;

import com.zeki.demo.liveworkouts.enums.LiveWorkoutEventType;

public record LiveWorkoutEventResponse(
        LiveWorkoutEventType type,
        String actorEmail,
        LiveWorkoutRoomResponse room,
        LiveWorkoutMessageResponse message
) {
    public LiveWorkoutEventResponse(LiveWorkoutEventType type, String actorEmail, LiveWorkoutRoomResponse room) {
        this(type, actorEmail, room, null);
    }

    public static LiveWorkoutEventResponse roomEvent(
            LiveWorkoutEventType type,
            String actorEmail,
            LiveWorkoutRoomResponse room
    ) {
        return new LiveWorkoutEventResponse(type, actorEmail, room);
    }

    public static LiveWorkoutEventResponse messageEvent(
            LiveWorkoutEventType type,
            String actorEmail,
            LiveWorkoutMessageResponse message
    ) {
        return new LiveWorkoutEventResponse(type, actorEmail, null, message);
    }
}
