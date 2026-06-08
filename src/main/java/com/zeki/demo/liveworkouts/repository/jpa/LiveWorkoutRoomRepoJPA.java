package com.zeki.demo.liveworkouts.repository.jpa;

import com.zeki.demo.liveworkouts.entity.LiveWorkoutRoomEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LiveWorkoutRoomRepoJPA extends JpaRepository<LiveWorkoutRoomEntity, String> {
    Optional<LiveWorkoutRoomEntity> findByRoomCodeAndStatus(String roomCode, String status);

    @Modifying
    @Query(value = "DELETE FROM live_workout_participants WHERE room_code = :roomCode", nativeQuery = true)
    void deleteParticipantsByRoomCode(@Param("roomCode") String roomCode);
}
