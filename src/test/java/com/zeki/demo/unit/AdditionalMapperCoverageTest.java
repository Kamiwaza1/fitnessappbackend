package com.zeki.demo.unit;

import com.zeki.demo.workouthistory.domain.WorkoutHistory;
import com.zeki.demo.workouthistory.dto.WorkoutHistoryResponse;
import com.zeki.demo.workouthistory.entity.WorkoutHistoryEntity;
import com.zeki.demo.workouthistory.mapperdtodomain.WorkoutHistoryDtoDomain;
import com.zeki.demo.workouthistory.mapperentitydomain.WorkoutHistoryEntityMapper;
import com.zeki.demo.exerciselibrary.domain.ExerciseImage;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibrary;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryItem;
import com.zeki.demo.exerciselibrary.domain.ExerciseLicense;
import com.zeki.demo.exerciselibrary.domain.ExerciseMuscle;
import com.zeki.demo.exerciselibrary.domain.ExerciseReference;
import com.zeki.demo.exerciselibrary.domain.ExerciseVideo;
import com.zeki.demo.exerciselibrary.dto.ExerciseLibraryItemResponse;
import com.zeki.demo.exerciselibrary.dto.ExerciseLibraryListRequest;
import com.zeki.demo.exerciselibrary.dto.ExerciseLibraryResponse;
import com.zeki.demo.exerciselibrary.dto.ExerciseLibrarySearchRequest;
import com.zeki.demo.exerciselibrary.entity.ExerciseImageEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseLibraryEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseLibraryItemEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseLicenseEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseMuscleEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseReferenceEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseVideoEntity;
import com.zeki.demo.exerciselibrary.mapperdtodomain.ExerciseLibraryDtoDomain;
import com.zeki.demo.exerciselibrary.mapperentitydomain.ExerciseLibraryEntityMapper;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutMessage;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutParticipant;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutMessageResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutRoomDetailResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutRoomResponse;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutMessageDocument;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutParticipantValue;
import com.zeki.demo.liveworkouts.entity.LiveWorkoutRoomEntity;
import com.zeki.demo.liveworkouts.mapperdtodomain.LiveWorkoutMessageMapper;
import com.zeki.demo.liveworkouts.mapperdtodomain.LiveWorkoutRoomMapper;
import com.zeki.demo.liveworkouts.mapperentitydomain.LiveWorkoutMessageEntityMapper;
import com.zeki.demo.liveworkouts.mapperentitydomain.LiveWorkoutRoomEntityMapper;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AdditionalMapperCoverageTest {

    @Test
    void exerciseLibraryMappersMapNestedObjectsBothWays() {
        ExerciseLibraryEntityMapper entityMapper = new ExerciseLibraryEntityMapper();
        ExerciseLibraryDtoDomain dtoMapper = new ExerciseLibraryDtoDomain();

        ExerciseLibraryEntity entity = new ExerciseLibraryEntity(List.of(exerciseEntity()), 1, 25);

        ExerciseLibrary domain = entityMapper.toDomain(entity);
        ExerciseLibraryResponse response = dtoMapper.toResponse(domain);

        assertEquals(1, domain.count());
        assertEquals(25, response.total());
        ExerciseLibraryItemResponse item = response.results().get(0);
        assertEquals("Bench Press", item.name());
        assertEquals("Chest", item.category().name());
        assertEquals("Pecs", item.muscles().get(0).nameEn());
        assertEquals("Triceps", item.secondaryMuscles().get(0).name());
        assertEquals("Barbell", item.equipment().get(0).name());
        assertEquals("bench.png", item.images().get(0).image());
        assertEquals("bench.mp4", item.videos().get(0).video());
        assertEquals("CC", item.license().shortName());
        assertEquals("wger", item.licenseAuthor());

        assertEquals(10, entityMapper.toMuscleDomains(List.of(new ExerciseMuscleEntity(
                10, "Quadriceps", "Quads", true, "main.svg", "secondary.svg"
        ))).get(0).id());
        assertEquals("Cable", entityMapper.toReferenceDomains(List.of(new ExerciseReferenceEntity(9, "Cable"))).get(0).name());

        assertEquals(50, dtoMapper.toDomain(new ExerciseLibraryListRequest(50, 5)).limit());
        assertEquals("push", dtoMapper.toDomain(new ExerciseLibrarySearchRequest("push", 20, 2, 1, 2, 3)).q());
    }

    @Test
    void exerciseLibraryMappersHandleNullableReferenceAndLicense() {
        ExerciseLibraryEntityMapper entityMapper = new ExerciseLibraryEntityMapper();
        ExerciseLibraryDtoDomain dtoMapper = new ExerciseLibraryDtoDomain();
        ExerciseLibraryItemEntity entity = new ExerciseLibraryItemEntity(
                2,
                "uuid",
                "Bodyweight",
                "desc",
                "source",
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                "author"
        );

        ExerciseLibraryItem domain = entityMapper.toDomain(entity);
        ExerciseLibraryItemResponse response = dtoMapper.toResponse(domain);

        assertNull(response.category());
        assertNull(response.license());
    }

    @Test
    void liveWorkoutMappersMapMessagesAndRooms() {
        Instant now = Instant.now();
        LiveWorkoutMessage message = new LiveWorkoutMessage("msg-1", "ROOM1", "user@example.com", "user", "hello", now);
        LiveWorkoutMessageMapper messageMapper = new LiveWorkoutMessageMapper();
        LiveWorkoutMessageEntityMapper messageEntityMapper = new LiveWorkoutMessageEntityMapper();

        LiveWorkoutMessageResponse messageResponse = messageMapper.toResponse(message);
        LiveWorkoutMessageDocument document = messageEntityMapper.toEntity(message);
        LiveWorkoutMessage mappedMessage = messageEntityMapper.toDomain(document);

        assertEquals("hello", messageResponse.text());
        assertEquals("ROOM1", document.getRoomCode());
        assertEquals("user", mappedMessage.senderName());

        Map<String, LiveWorkoutParticipant> participants = new LinkedHashMap<>();
        participants.put("user@example.com", new LiveWorkoutParticipant("user@example.com", "user", "ACTIVE", 1, 2, now));
        LiveWorkoutRoom room = new LiveWorkoutRoom("ROOM1", "host@example.com", UUID.randomUUID(), "Push", participants, now, now);
        LiveWorkoutRoomMapper roomMapper = new LiveWorkoutRoomMapper();

        LiveWorkoutRoomResponse roomResponse = roomMapper.toResponse(room);
        LiveWorkoutRoomDetailResponse detailResponse = roomMapper.toDetailResponse(room, List.of(messageResponse));

        assertEquals("Push", roomResponse.workoutName());
        assertEquals("user", roomResponse.participants().get(0).displayName());
        assertEquals("hello", detailResponse.messages().get(0).text());
    }

    @Test
    void liveWorkoutRoomEntityMapperSortsParticipantsAndMapsBothWays() {
        Instant earlier = Instant.now().minusSeconds(60);
        Instant later = Instant.now();
        UUID workoutId = UUID.randomUUID();
        LiveWorkoutRoomEntity entity = LiveWorkoutRoomEntity.builder()
                .roomCode("ROOM2")
                .hostEmail("host@example.com")
                .workoutId(workoutId)
                .workoutName("Legs")
                .status("ACTIVE")
                .createdAt(earlier)
                .lastActivityAt(later)
                .participants(List.of(
                        participantValue("second@example.com", "second", later),
                        participantValue("first@example.com", "first", earlier)
                ))
                .build();
        LiveWorkoutRoomEntityMapper mapper = new LiveWorkoutRoomEntityMapper();

        LiveWorkoutRoom room = mapper.toDomain(entity);
        LiveWorkoutRoomEntity mappedEntity = mapper.toEntity(room, "CLOSED", later);

        assertEquals("first@example.com", room.participants().keySet().iterator().next());
        assertEquals("CLOSED", mappedEntity.getStatus());
        assertEquals(2, mappedEntity.getParticipants().size());
    }

    @Test
    void liveWorkoutRoomEntityMapperDefaultsNullParticipantFields() {
        Instant createdAt = Instant.now().minusSeconds(60);
        Instant lastActivityAt = Instant.now();
        UUID workoutId = UUID.randomUUID();
        LiveWorkoutRoomEntity entity = LiveWorkoutRoomEntity.builder()
                .roomCode("ROOM3")
                .hostEmail("host@example.com")
                .workoutId(workoutId)
                .workoutName("Push")
                .status("ACTIVE")
                .createdAt(createdAt)
                .lastActivityAt(lastActivityAt)
                .participants(new java.util.ArrayList<>(java.util.Arrays.asList(null, LiveWorkoutParticipantValue.builder()
                        .email("user@example.com")
                        .displayName(null)
                        .status(null)
                        .currentExerciseIndex(null)
                        .completedSets(null)
                        .joinedAt(null)
                        .build())))
                .build();

        LiveWorkoutRoom room = new LiveWorkoutRoomEntityMapper().toDomain(entity);
        LiveWorkoutParticipant participant = room.participants().get("user@example.com");

        assertEquals("user", participant.displayName());
        assertEquals("ACTIVE", participant.status());
        assertEquals(0, participant.currentExerciseIndex());
        assertEquals(0, participant.completedSets());
        assertEquals(createdAt, participant.joinedAt());
    }

    @Test
    void workoutHistoryMappersMapEntityAndResponse() {
        UUID workoutId = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        WorkoutHistoryEntity entity = WorkoutHistoryEntity.builder()
                .workoutId(workoutId)
                .workoutName("Pull Day")
                .description("desc")
                .category(WorkoutCategory.STRENGTH)
                .muscleType(MuscleType.BACK)
                .createdAt(now.minusDays(2))
                .updatedAt(now.minusDays(1))
                .userEmail("user@example.com")
                .workoutDate(now)
                .durationMinutes(45)
                .sets(5)
                .reps(50)
                .weightKg(90.0)
                .notes("notes")
                .build();

        WorkoutHistory history = new WorkoutHistoryEntityMapper().toDomain(entity);
        WorkoutHistoryResponse response = new WorkoutHistoryDtoDomain().toResponse(history);

        assertEquals(workoutId, response.workoutId());
        assertEquals("Pull Day", response.workoutName());
        assertEquals(MuscleType.BACK, response.muscleType());
    }

    private ExerciseLibraryItemEntity exerciseEntity() {
        return new ExerciseLibraryItemEntity(
                1,
                "uuid",
                "Bench Press",
                "desc",
                "source",
                new ExerciseReferenceEntity(1, "Chest"),
                List.of(new ExerciseMuscleEntity(2, "Pectoralis", "Pecs", true, "main.svg", "secondary.svg")),
                List.of(new ExerciseMuscleEntity(3, "Triceps", "Triceps", true, "main.svg", "secondary.svg")),
                List.of(new ExerciseReferenceEntity(4, "Barbell")),
                List.of("Bench"),
                List.of(new ExerciseImageEntity(5, "bench.png", true)),
                List.of(new ExerciseVideoEntity(6, "bench.mp4", false, "30s")),
                new ExerciseLicenseEntity(7, "Creative Commons", "CC", "https://license.test"),
                "wger"
        );
    }

    private LiveWorkoutParticipantValue participantValue(String email, String displayName, Instant joinedAt) {
        return LiveWorkoutParticipantValue.builder()
                .email(email)
                .displayName(displayName)
                .status("ACTIVE")
                .currentExerciseIndex(0)
                .completedSets(0)
                .joinedAt(joinedAt)
                .build();
    }
}
