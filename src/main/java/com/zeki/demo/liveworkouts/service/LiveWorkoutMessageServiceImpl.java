package com.zeki.demo.liveworkouts.service;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutMessage;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.domain.repository.LiveWorkoutMessageRepository;
import com.zeki.demo.liveworkouts.exception.LiveWorkoutAccessException;
import com.zeki.demo.liveworkouts.exception.LiveWorkoutValidationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LiveWorkoutMessageServiceImpl implements LiveWorkoutMessageService {

    private static final int RECENT_MESSAGE_LIMIT = 100;

    private final LiveWorkoutRoomService liveWorkoutRoomService;
    private final LiveWorkoutMessageRepository liveWorkoutMessageRepository;

    @Override
    public LiveWorkoutMessage sendMessage(String roomCode, String senderEmail, String text) {
        if (text == null || text.isBlank()) {
            throw new LiveWorkoutValidationException("Message text is required");
        }
        String trimmedText = text.trim();
        if (trimmedText.length() > 500) {
            throw new LiveWorkoutValidationException("Message cannot be longer than 500 characters");
        }
        LiveWorkoutRoom room = liveWorkoutRoomService.joinRoom(roomCode, senderEmail);
        requireParticipant(room, senderEmail);
        return liveWorkoutMessageRepository.save(LiveWorkoutMessage.create(room.roomCode(), senderEmail, trimmedText));
    }

    @Override
    public List<LiveWorkoutMessage> getRecentMessages(String roomCode, String userEmail) {
        LiveWorkoutRoom room = liveWorkoutRoomService.getRoom(roomCode);
        return liveWorkoutMessageRepository.findRecentByRoomCode(room.roomCode(), RECENT_MESSAGE_LIMIT);
    }

    private void requireParticipant(LiveWorkoutRoom room, String userEmail) {
        if (!room.participants().containsKey(userEmail)) {
            throw new LiveWorkoutAccessException("Join the live workout before using chat");
        }
    }
}
