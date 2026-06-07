package com.zeki.demo.liveworkouts.controller;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutEventType;
import com.zeki.demo.liveworkouts.dto.CreateLiveWorkoutRoomRequest;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutEventResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutProgressRequest;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutRoomResponse;
import com.zeki.demo.liveworkouts.mapperDtoDomain.LiveWorkoutRoomMapper;
import com.zeki.demo.liveworkouts.service.LiveWorkoutRoomService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/live-workouts")
@RequiredArgsConstructor
public class LiveWorkoutRoomController {

    private final LiveWorkoutRoomService liveWorkoutRoomService;
    private final LiveWorkoutRoomMapper liveWorkoutRoomMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LiveWorkoutRoomResponse createRoom(
            @Valid @RequestBody CreateLiveWorkoutRoomRequest request,
            Authentication authentication
    ) {
        return liveWorkoutRoomMapper.toResponse(
                liveWorkoutRoomService.createRoom(request.workoutId(), authentication.getName())
        );
    }

    @GetMapping("/{roomCode}")
    public LiveWorkoutRoomResponse getRoom(@PathVariable String roomCode) {
        return liveWorkoutRoomMapper.toResponse(liveWorkoutRoomService.getRoom(roomCode));
    }

    @DeleteMapping("/{roomCode}")
    public LiveWorkoutRoomResponse closeRoom(@PathVariable String roomCode, Authentication authentication) {
        LiveWorkoutRoomResponse room = liveWorkoutRoomMapper.toResponse(
                liveWorkoutRoomService.closeRoom(roomCode, authentication.getName())
        );
        broadcast(roomCode, new LiveWorkoutEventResponse(LiveWorkoutEventType.ROOM_CLOSED, authentication.getName(), room));
        return room;
    }

    @MessageMapping("/live-workouts/{roomCode}/join")
    public void joinRoom(@DestinationVariable String roomCode, Principal principal) {
        LiveWorkoutRoomResponse room = liveWorkoutRoomMapper.toResponse(
                liveWorkoutRoomService.joinRoom(roomCode, principal.getName())
        );
        broadcast(roomCode, new LiveWorkoutEventResponse(LiveWorkoutEventType.USER_JOINED, principal.getName(), room));
    }

    @MessageMapping("/live-workouts/{roomCode}/leave")
    public void leaveRoom(@DestinationVariable String roomCode, Principal principal) {
        LiveWorkoutRoomResponse room = liveWorkoutRoomMapper.toResponse(
                liveWorkoutRoomService.leaveRoom(roomCode, principal.getName())
        );
        broadcast(roomCode, new LiveWorkoutEventResponse(LiveWorkoutEventType.USER_LEFT, principal.getName(), room));
    }

    @MessageMapping("/live-workouts/{roomCode}/progress")
    public void updateProgress(
            @DestinationVariable String roomCode,
            @Payload LiveWorkoutProgressRequest request,
            Principal principal
    ) {
        LiveWorkoutRoomResponse room = liveWorkoutRoomMapper.toResponse(
                liveWorkoutRoomService.updateProgress(roomCode, principal.getName(), request)
        );
        broadcast(roomCode, new LiveWorkoutEventResponse(LiveWorkoutEventType.PROGRESS_UPDATED, principal.getName(), room));
    }

    private void broadcast(String roomCode, LiveWorkoutEventResponse event) {
        messagingTemplate.convertAndSend("/topic/live-workouts/" + roomCode.toUpperCase(), event);
    }
}
