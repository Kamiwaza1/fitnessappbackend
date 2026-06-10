package com.zeki.demo.mail;

import com.zeki.demo.users.domain.User;

public interface EmailService {
    void sendWelcomeEmail(User user);

    void sendPasswordResetEmail(User user, String resetUrl, String securityCode, long expirationMinutes);
}
