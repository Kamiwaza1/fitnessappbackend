package com.zeki.demo.unit;

import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutProgressRequest;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutRoomEntity;
import com.zeki.demo.liveworkouts.mapperentitydomain.LiveWorkoutRoomEntityMapper;
import com.zeki.demo.liveworkouts.repository.LiveWorkoutRoomRepository;
import com.zeki.demo.liveworkouts.repository.LiveWorkoutRoomRepositoryImpl;
import com.zeki.demo.liveworkouts.repository.jpa.LiveWorkoutRoomRepoJPA;
import com.zeki.demo.liveworkouts.service.LiveWorkoutRoomService;
import com.zeki.demo.liveworkouts.service.LiveWorkoutRoomServiceImpl;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.service.WorkoutService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class LiveWorkoutRoomServiceTest {

    private static final String HOST_EMAIL = "host@example.com";
    private static final String GUEST_EMAIL = "guest@example.com";

    @Mock
    private WorkoutService workoutService;

    @Mock
    private LiveWorkoutRoomRepoJPA liveWorkoutRoomRepoJPA;

    private Map<String, LiveWorkoutRoomEntity> savedRooms;
    private LiveWorkoutRoomService liveWorkoutRoomService;

    @BeforeEach
    void setUp() {
        savedRooms = new HashMap<>();
        lenient().when(liveWorkoutRoomRepoJPA.existsById(any(String.class))).thenAnswer(invocation ->
                savedRooms.containsKey(invocation.getArgument(0))
        );
        lenient().when(liveWorkoutRoomRepoJPA.save(any(LiveWorkoutRoomEntity.class))).thenAnswer(invocation -> {
            LiveWorkoutRoomEntity entity = invocation.getArgument(0);
            savedRooms.put(entity.getRoomCode(), entity);
            return entity;
        });
        lenient().when(liveWorkoutRoomRepoJPA.findByRoomCodeAndStatus(any(String.class), any(String.class))).thenAnswer(invocation -> {
            LiveWorkoutRoomEntity entity = savedRooms.get(invocation.getArgument(0));
            String status = invocation.getArgument(1);
            return entity != null && status.equals(entity.getStatus()) ? Optional.of(entity) : Optional.empty();
        });
        LiveWorkoutRoomRepository liveWorkoutRoomRepository = new LiveWorkoutRoomRepositoryImpl(
                liveWorkoutRoomRepoJPA,
                new LiveWorkoutRoomEntityMapper()
        );
        liveWorkoutRoomService = new LiveWorkoutRoomServiceImpl(workoutService, liveWorkoutRoomRepository);
    }

    @Test
    void createRoomRequiresWorkoutOwnership() {
        UUID workoutId = UUID.randomUUID();
        when(workoutService.getWorkoutById(workoutId, HOST_EMAIL)).thenReturn(validWorkout(workoutId));

        LiveWorkoutRoom room = liveWorkoutRoomService.createRoom(workoutId, HOST_EMAIL);

        assertEquals(HOST_EMAIL, room.hostEmail());
        assertEquals(workoutId, room.workoutId());
        assertTrue(room.participants().containsKey(HOST_EMAIL));
        verify(workoutService).getWorkoutById(workoutId, HOST_EMAIL);
    }

    @Test
    void joinRoomAddsParticipant() {
        LiveWorkoutRoom room = createRoom();

        LiveWorkoutRoom updated = liveWorkoutRoomService.joinRoom(room.roomCode(), GUEST_EMAIL);

        assertTrue(updated.participants().containsKey(HOST_EMAIL));
        assertTrue(updated.participants().containsKey(GUEST_EMAIL));
        verify(liveWorkoutRoomRepoJPA).deleteParticipantsByRoomCode(room.roomCode());
        verify(liveWorkoutRoomRepoJPA).flush();
    }

    @Test
    void progressUpdateRequiresJoinedUser() {
        LiveWorkoutRoom room = createRoom();

        assertThrows(
                UnauthorizedException.class,
                () -> liveWorkoutRoomService.updateProgress(
                        room.roomCode(),
                        GUEST_EMAIL,
                        new LiveWorkoutProgressRequest(1, 2, "ACTIVE")
                )
        );
    }

    @Test
    void progressUpdateStoresParticipantProgress() {
        LiveWorkoutRoom room = createRoom();
        liveWorkoutRoomService.joinRoom(room.roomCode(), GUEST_EMAIL);

        LiveWorkoutRoom updated = liveWorkoutRoomService.updateProgress(
                room.roomCode(),
                GUEST_EMAIL,
                new LiveWorkoutProgressRequest(2, 4, "RESTING")
        );

        assertEquals(2, updated.participants().get(GUEST_EMAIL).currentExerciseIndex());
        assertEquals(4, updated.participants().get(GUEST_EMAIL).completedSets());
        assertEquals("RESTING", updated.participants().get(GUEST_EMAIL).status());
    }

    @Test
    void onlyHostCanCloseRoom() {
        LiveWorkoutRoom room = createRoom();

        assertThrows(UnauthorizedException.class, () -> liveWorkoutRoomService.closeRoom(room.roomCode(), GUEST_EMAIL));

        LiveWorkoutRoom closed = liveWorkoutRoomService.closeRoom(room.roomCode(), HOST_EMAIL);

        assertEquals(room.roomCode(), closed.roomCode());
        assertFalse(closed.participants().isEmpty());
    }

    private LiveWorkoutRoom createRoom() {
        UUID workoutId = UUID.randomUUID();
        when(workoutService.getWorkoutById(workoutId, HOST_EMAIL)).thenReturn(validWorkout(workoutId));
        return liveWorkoutRoomService.createRoom(workoutId, HOST_EMAIL);
    }

    private Workout validWorkout(UUID workoutId) {
        LocalDate now = LocalDate.now();
        return new Workout(
                "Live workout",
                now,
                45,
                4,
                10,
                80.0,
                "notes",
                workoutId,
                "description",
                WorkoutCategory.STRENGTH,
                MuscleType.FULL_BODY,
                null,
                now,
                now,
                HOST_EMAIL
        );
    }
}
