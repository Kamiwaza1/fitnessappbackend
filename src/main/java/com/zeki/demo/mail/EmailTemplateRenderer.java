package com.zeki.demo.mail;

import com.zeki.demo.users.domain.User;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

@Component
public class EmailTemplateRenderer {

    public String welcome(User user) {
        return render("email/welcome.html", Map.of(
                "displayName", escapedDisplayName(user)
        ));
    }

    public String passwordReset(User user, String resetUrl, String securityCode, long expirationMinutes) {
        return render("email/password-reset.html", Map.of(
                "displayName", escapedDisplayName(user),
                "resetUrl", HtmlUtils.htmlEscape(resetUrl),
                "securityCode", HtmlUtils.htmlEscape(securityCode),
                "expirationMinutes", String.valueOf(expirationMinutes)
        ));
    }

    public String accountDeletion(User user, String publicLoginUrl, String localLoginUrl, long gracePeriodDays) {
        String safeLocalLoginUrl = HtmlUtils.htmlEscape(localLoginUrl);
        return render("email/account-deletion.html", Map.of(
                "displayName", escapedDisplayName(user),
                "publicLoginUrl", HtmlUtils.htmlEscape(publicLoginUrl),
                "localLoginUrl", safeLocalLoginUrl,
                "localLoginUrlLabel", safeLocalLoginUrl,
                "gracePeriodDays", String.valueOf(gracePeriodDays)
        ));
    }

    private String render(String templatePath, Map<String, String> values) {
        String html = loadTemplate(templatePath);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            html = html.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return html;
    }

    private String loadTemplate(String templatePath) {
        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException("Email template could not be loaded: " + templatePath, exception);
        }
    }

    private String escapedDisplayName(User user) {
        return HtmlUtils.htmlEscape(User.resolveFullName(user.getFullName(), user.getEmail()));
    }
}
