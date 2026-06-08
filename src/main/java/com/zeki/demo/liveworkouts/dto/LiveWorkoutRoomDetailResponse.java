package com.zeki.demo.liveworkouts.dto;

import java.util.List;

public record LiveWorkoutRoomDetailResponse(
        LiveWorkoutRoomResponse room,
        List<LiveWorkoutMessageResponse> messages
) {
}
