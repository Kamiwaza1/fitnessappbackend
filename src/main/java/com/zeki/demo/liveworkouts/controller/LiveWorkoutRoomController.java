package com.zeki.demo.liveworkouts.controller;

import com.zeki.demo.liveworkouts.dto.CreateLiveWorkoutRoomRequest;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutMessageRequest;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutMessageResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutProgressRequest;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutRoomDetailResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutRoomResponse;
import com.zeki.demo.liveworkouts.mapperdtodomain.LiveWorkoutMessageMapper;
import com.zeki.demo.liveworkouts.mapperdtodomain.LiveWorkoutRoomMapper;
import com.zeki.demo.liveworkouts.service.LiveWorkoutEventPublisher;
import com.zeki.demo.liveworkouts.service.LiveWorkoutMessageService;
import com.zeki.demo.liveworkouts.service.LiveWorkoutRoomService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
    private final LiveWorkoutMessageService liveWorkoutMessageService;
    private final LiveWorkoutRoomMapper liveWorkoutRoomMapper;
    private final LiveWorkoutMessageMapper liveWorkoutMessageMapper;
    private final LiveWorkoutEventPublisher liveWorkoutEventPublisher;

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveWorkoutRoomController.class);

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

    @GetMapping("/{roomCode}/details")
    public LiveWorkoutRoomDetailResponse getRoomDetails(
            @PathVariable String roomCode,
            Authentication authentication
    ) {
        return liveWorkoutRoomMapper.toDetailResponse(
                liveWorkoutRoomService.getRoom(roomCode),
                liveWorkoutMessageService.getRecentMessages(roomCode, authentication.getName())
                        .stream()
                        .map(liveWorkoutMessageMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{roomCode}/messages")
    public List<LiveWorkoutMessageResponse> getMessages(
            @PathVariable String roomCode,
            Authentication authentication
    ) {
        return liveWorkoutMessageService.getRecentMessages(roomCode, authentication.getName())
                .stream()
                .map(liveWorkoutMessageMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{roomCode}")
    public LiveWorkoutRoomResponse closeRoom(@PathVariable String roomCode, Authentication authentication) {
        LiveWorkoutRoomResponse room = liveWorkoutRoomMapper.toResponse(
                liveWorkoutRoomService.closeRoom(roomCode, authentication.getName())
        );
        liveWorkoutEventPublisher.publishRoomClosed(roomCode, authentication.getName(), room);
        return room;
    }

    @MessageMapping("/live-workouts/{roomCode}/join")
    public void joinRoom(@DestinationVariable String roomCode, Principal principal, @Header("simpSessionId") String sessionId) {
        String userEmail = principal.getName();
        LOGGER.debug("WebSocket joinRoom: room={} sessionId={} user={}", roomCode, sessionId, userEmail);
        LiveWorkoutRoomResponse room = liveWorkoutRoomMapper.toResponse(
                liveWorkoutRoomService.joinRoom(roomCode, userEmail)
        );
        liveWorkoutEventPublisher.publishUserJoined(roomCode, userEmail, room);
    }

    @MessageMapping("/live-workouts/{roomCode}/leave")
    public void leaveRoom(@DestinationVariable String roomCode, Principal principal, @Header("simpSessionId") String sessionId) {
        String userEmail = principal.getName();
        LOGGER.debug("WebSocket leaveRoom: room={} sessionId={} user={}", roomCode, sessionId, userEmail);
        LiveWorkoutRoomResponse room = liveWorkoutRoomMapper.toResponse(
                liveWorkoutRoomService.leaveRoom(roomCode, userEmail)
        );
        liveWorkoutEventPublisher.publishUserLeft(roomCode, userEmail, room);
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
        liveWorkoutEventPublisher.publishProgressUpdated(roomCode, principal.getName(), room);
    }

    @MessageMapping("/live-workouts/{roomCode}/messages")
    public void sendMessage(
            @DestinationVariable String roomCode,
            @Valid @Payload LiveWorkoutMessageRequest request,
            Principal principal
    ) {
        LiveWorkoutMessageResponse message = liveWorkoutMessageMapper.toResponse(
                liveWorkoutMessageService.sendMessage(roomCode, principal.getName(), request.text())
        );
        liveWorkoutEventPublisher.publishMessageSent(roomCode, principal.getName(), message);
    }
}
