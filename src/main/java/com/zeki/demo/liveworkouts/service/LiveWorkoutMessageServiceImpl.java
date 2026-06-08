package com.zeki.demo.liveworkouts.service;

import com.zeki.demo.common.exception.BadRequestException;
import com.zeki.demo.common.exception.UnauthorizedException;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutMessage;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.repository.LiveWorkoutMessageRepository;
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
        LiveWorkoutRoom room = liveWorkoutRoomService.getRoom(roomCode);
        requireParticipant(room, senderEmail);
        if (text == null || text.isBlank()) {
            throw new BadRequestException("Message text is required");
        }
        if (text.trim().length() > 500) {
            throw new BadRequestException("Message cannot be longer than 500 characters");
        }
        return liveWorkoutMessageRepository.save(LiveWorkoutMessage.create(room.roomCode(), senderEmail, text));
    }

    @Override
    public List<LiveWorkoutMessage> getRecentMessages(String roomCode, String userEmail) {
        LiveWorkoutRoom room = liveWorkoutRoomService.getRoom(roomCode);
        requireParticipant(room, userEmail);
        return liveWorkoutMessageRepository.findRecentByRoomCode(room.roomCode(), RECENT_MESSAGE_LIMIT);
    }

    private void requireParticipant(LiveWorkoutRoom room, String userEmail) {
        if (!room.participants().containsKey(userEmail)) {
            throw new UnauthorizedException("Join the live workout before using chat");
        }
    }
}
