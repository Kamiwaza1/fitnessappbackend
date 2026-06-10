package com.zeki.demo.mail;

import com.zeki.demo.users.domain.User;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class ResendEmailService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendEmailService.class);

    private final WebClient webClient;
    private final String apiKey;
    private final String from;
    private final boolean enabled;
    private final boolean debug;
    private final EmailTemplateRenderer emailTemplateRenderer;

    public ResendEmailService(
            WebClient.Builder builder,
            @Value("${mail.resend.base-url:https://api.resend.com}") String baseUrl,
            @Value("${mail.resend.api-key:}") String apiKey,
            @Value("${mail.from:DarkLift <noreply@darklift.app>}") String from,
            @Value("${mail.enabled:true}") boolean enabled,
            @Value("${mail.debug:false}") boolean debug,
            EmailTemplateRenderer emailTemplateRenderer
    ) {
        this.webClient = builder
                .clone()
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
        this.from = from;
        this.enabled = enabled;
        this.debug = debug;
        this.emailTemplateRenderer = emailTemplateRenderer;
    }

    @Override
    public void sendWelcomeEmail(User user) {
        sendEmail(
                new EmailRequest(
                        from,
                        List.of(user.getEmail()),
                        "Welcome to DarkLift",
                        emailTemplateRenderer.welcome(user)
                ),
                user.getEmail(),
                "Welcome"
        );
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetUrl, String securityCode, long expirationMinutes) {
        if (debug) {
            LOGGER.info("MAIL_DEBUG password reset for {}: url={} code={}", user.getEmail(), resetUrl, securityCode);
        }
        sendEmail(
                new EmailRequest(
                        from,
                        List.of(user.getEmail()),
                        "Reset your DarkLift password",
                        emailTemplateRenderer.passwordReset(user, resetUrl, securityCode, expirationMinutes)
                ),
                user.getEmail(),
                "Password reset"
        );
    }

    private void sendEmail(EmailRequest request, String recipient, String purpose) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            LOGGER.info("{} email skipped because mail is disabled or RESEND_API_KEY is missing", purpose);
            return;
        }

        try {
            ResendEmailResponse response = webClient.post()
                    .uri("/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ResendEmailResponse.class)
                    .block();
            LOGGER.info("{} email sent to {} with Resend id {}", purpose, recipient, response != null ? response.id() : "unknown");
        } catch (WebClientResponseException exception) {
            LOGGER.warn(
                    "{} email could not be sent to {}. Resend responded with status {} and body {}",
                    purpose,
                    recipient,
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString()
            );
        } catch (RuntimeException exception) {
            LOGGER.warn("{} email could not be sent to {}", purpose, recipient, exception);
        }
    }

    private record EmailRequest(
            String from,
            List<String> to,
            String subject,
            String html
    ) {
    }

    private record ResendEmailResponse(String id) {
    }
}
