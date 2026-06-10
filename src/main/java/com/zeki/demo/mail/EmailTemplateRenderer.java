package com.zeki.demo.mail;

import com.zeki.demo.users.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
public class EmailTemplateRenderer {

    public String welcome(User user) {
        String displayName = escapedDisplayName(user);
        String content = """
                <h1 style="margin:0 0 14px;font-size:28px;line-height:1.15;color:#f8fafc;">Welcome to DarkLift, %s</h1>
                <p style="margin:0 0 16px;color:#cbd5e1;font-size:16px;line-height:1.6;">
                  Your account is ready. You can now track workouts, nutrition, and progress in one place.
                </p>
                <p style="margin:0;color:#94a3b8;font-size:15px;line-height:1.6;">
                  Keep showing up. Small sessions add up.
                </p>
                """.formatted(displayName);

        return layout("Welcome to DarkLift", "Your training space is ready.", content);
    }

    public String passwordReset(User user, String resetUrl, String securityCode, long expirationMinutes) {
        String displayName = escapedDisplayName(user);
        String safeResetUrl = HtmlUtils.htmlEscape(resetUrl);
        String safeSecurityCode = HtmlUtils.htmlEscape(securityCode);
        String content = """
                <h1 style="margin:0 0 14px;font-size:28px;line-height:1.15;color:#f8fafc;">Reset your password</h1>
                <p style="margin:0 0 16px;color:#cbd5e1;font-size:16px;line-height:1.6;">
                  Hi %s, use this security code and button to set a new DarkLift password.
                </p>
                <div style="margin:22px 0;padding:18px;border:1px solid #334155;border-radius:12px;background:#0f172a;text-align:center;">
                  <p style="margin:0 0 8px;color:#94a3b8;font-size:13px;text-transform:uppercase;letter-spacing:1.6px;">Security code</p>
                  <p style="margin:0;color:#f8fafc;font-size:32px;font-weight:800;letter-spacing:8px;">%s</p>
                </div>
                %s
                <p style="margin:18px 0 0;color:#94a3b8;font-size:14px;line-height:1.6;">
                  This link expires in %d minutes and can only be used once. If you did not request this, you can ignore this email.
                </p>
                """.formatted(
                displayName,
                safeSecurityCode,
                button("Reset password", safeResetUrl),
                expirationMinutes
        );

        return layout("Reset your DarkLift password", "Secure password recovery", content);
    }

    private String layout(String title, String preheader, String content) {
        return """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>%s</title>
                  </head>
                  <body style="margin:0;padding:0;background:#020617;font-family:Arial,Helvetica,sans-serif;">
                    <div style="display:none;max-height:0;overflow:hidden;opacity:0;color:transparent;">%s</div>
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#020617;margin:0;padding:32px 16px;">
                      <tr>
                        <td align="center">
                          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:560px;background:#111827;border:1px solid #1f2937;border-radius:18px;overflow:hidden;">
                            <tr>
                              <td style="padding:28px 28px 16px;border-bottom:1px solid #1f2937;">
                                <p style="margin:0;color:#ef4444;font-size:22px;font-weight:900;font-style:italic;letter-spacing:.4px;">DARK LIFT</p>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:30px 28px;">
                                %s
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:20px 28px;background:#0b1120;border-top:1px solid #1f2937;">
                                <p style="margin:0;color:#64748b;font-size:12px;line-height:1.5;">
                                  This email was sent by DarkLift. Keep this message private if it contains a security code.
                                </p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """.formatted(
                HtmlUtils.htmlEscape(title),
                HtmlUtils.htmlEscape(preheader),
                content
        );
    }

    private String button(String label, String url) {
        return """
                <p style="margin:24px 0 0;">
                  <a href="%s" style="display:inline-block;background:#dc2626;color:#ffffff;text-decoration:none;padding:13px 20px;border-radius:999px;font-weight:800;font-size:15px;">
                    %s
                  </a>
                </p>
                """.formatted(url, HtmlUtils.htmlEscape(label));
    }

    private String escapedDisplayName(User user) {
        return HtmlUtils.htmlEscape(User.resolveFullName(user.getFullName(), user.getEmail()));
    }
}
