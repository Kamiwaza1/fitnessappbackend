package com.zeki.demo.liveworkouts.service;

import com.zeki.demo.liveworkouts.domain.LiveWorkoutMessage;
import java.util.List;

public interface LiveWorkoutMessageService {

    LiveWorkoutMessage sendMessage(String roomCode, String senderEmail, String text);

    List<LiveWorkoutMessage> getRecentMessages(String roomCode, String userEmail);
}
