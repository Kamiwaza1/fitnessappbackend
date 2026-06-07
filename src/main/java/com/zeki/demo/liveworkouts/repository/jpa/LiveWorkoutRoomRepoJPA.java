package com.zeki.demo.liveworkouts.repository.jpa;

import com.zeki.demo.liveworkouts.entity.LiveWorkoutRoomEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveWorkoutRoomRepoJPA extends JpaRepository<LiveWorkoutRoomEntity, String> {
    Optional<LiveWorkoutRoomEntity> findByRoomCodeAndStatus(String roomCode, String status);
}
