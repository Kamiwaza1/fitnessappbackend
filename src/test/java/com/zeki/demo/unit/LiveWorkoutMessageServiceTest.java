package com.zeki.demo.unit;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutMessage;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.repository.LiveWorkoutMessageRepository;
import com.zeki.demo.liveworkouts.service.LiveWorkoutMessageService;
import com.zeki.demo.liveworkouts.service.LiveWorkoutMessageServiceImpl;
import com.zeki.demo.liveworkouts.service.LiveWorkoutRoomService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

class LiveWorkoutMessageServiceTest {

    private static final String ROOM_CODE = "ROOM1";
    private static final String HOST_EMAIL = "host@example.com";
    private static final String GUEST_EMAIL = "guest@example.com";

    private LiveWorkoutRoomService liveWorkoutRoomService;
    private LiveWorkoutMessageRepository liveWorkoutMessageRepository;
    private LiveWorkoutMessageService liveWorkoutMessageService;

    @BeforeEach
    void setUp() {
        liveWorkoutRoomService = mock(LiveWorkoutRoomService.class);
        liveWorkoutMessageRepository = mock(LiveWorkoutMessageRepository.class);
        liveWorkoutMessageService = new LiveWorkoutMessageServiceImpl(
                liveWorkoutRoomService,
                liveWorkoutMessageRepository
        );
    }

    @Test
    void sendMessageRequiresParticipantAndStoresMessage() {
        LiveWorkoutRoom room = LiveWorkoutRoom.create(ROOM_CODE, HOST_EMAIL, UUID.randomUUID(), "Push");
        LiveWorkoutMessage saved = LiveWorkoutMessage.create(ROOM_CODE, HOST_EMAIL, "Nice set");

        when(liveWorkoutRoomService.getRoom(ROOM_CODE)).thenReturn(room);
        when(liveWorkoutMessageRepository.save(any(LiveWorkoutMessage.class))).thenReturn(saved);

        LiveWorkoutMessage message = liveWorkoutMessageService.sendMessage(ROOM_CODE, HOST_EMAIL, "Nice set");

        assertEquals("Nice set", message.text());
        verify(liveWorkoutMessageRepository).save(any(LiveWorkoutMessage.class));
    }

    @Test
    void sendMessageRejectsNonParticipantAndBlankText() {
        LiveWorkoutRoom room = LiveWorkoutRoom.create(ROOM_CODE, HOST_EMAIL, UUID.randomUUID(), "Push");
        when(liveWorkoutRoomService.getRoom(ROOM_CODE)).thenReturn(room);

        assertThrows(
                UnauthorizedException.class,
                () -> liveWorkoutMessageService.sendMessage(ROOM_CODE, GUEST_EMAIL, "hello")
        );
        assertThrows(
                BadRequestException.class,
                () -> liveWorkoutMessageService.sendMessage(ROOM_CODE, HOST_EMAIL, " ")
        );
    }

    @Test
    void getRecentMessagesAllowsAuthenticatedRoomViewers() {
        LiveWorkoutRoom room = LiveWorkoutRoom.create(ROOM_CODE, HOST_EMAIL, UUID.randomUUID(), "Push");
        LiveWorkoutMessage message = LiveWorkoutMessage.create(ROOM_CODE, HOST_EMAIL, "hello");

        when(liveWorkoutRoomService.getRoom(ROOM_CODE)).thenReturn(room);
        when(liveWorkoutMessageRepository.findRecentByRoomCode(ROOM_CODE, 100)).thenReturn(List.of(message));

        assertEquals(List.of(message), liveWorkoutMessageService.getRecentMessages(ROOM_CODE, HOST_EMAIL));
        assertEquals(List.of(message), liveWorkoutMessageService.getRecentMessages(ROOM_CODE, GUEST_EMAIL));
    }
}
