# Password Reset Flow

DarkLift uses a one-time opaque token for password reset. The token is not a JWT and does not contain user data.

## Endpoints

### Request reset link

`POST /api/v1/auth/forgot-password`

Request:

```json
{
  "email": "user@example.com"
}
```

Response:

```json
{
  "message": "If this email exists, a reset link has been sent."
}
```

The response is intentionally the same whether the account exists or not. This prevents user enumeration.

### Reset password

`POST /api/v1/auth/reset-password`

Request:

```json
{
  "token": "raw-token-from-email-link",
  "code": "123456",
  "newPassword": "NewStrongPassword123!"
}
```

Response:

```json
{
  "message": "Password has been reset successfully."
}
```

## Token Model

The email contains two pieces:

- A reset button with the raw token in the URL.
- A 6-digit security code that the user must type before choosing a new password.

```text
http://localhost:3000/reset-password?token=...
https://darklift.app/reset-password?token=...
```

The database stores only hashes:

- `SHA-256(rawToken)` in `password_reset_tokens.token_hash`.
- `SHA-256(securityCode)` in `password_reset_tokens.code_hash`.

Token rules:

- 32 random bytes generated with `SecureRandom`.
- URL-safe Base64 without padding.
- Stored as SHA-256 hash, never plaintext.
- 6-digit security code stored as SHA-256 hash, never plaintext.
- Expires after `PASSWORD_RESET_EXPIRATION_MINUTES`, default `30`.
- One-time use through `used_at`.
- Existing active reset tokens for the same user are marked used when a new link is requested.

## Environment

Backend:

```bash
RESEND_API_KEY="..."
MAIL_FROM="DarkLift <noreply@darklift.app>"
APP_FRONTEND_URL="http://localhost:3000"
PASSWORD_RESET_EXPIRATION_MINUTES=30
```

Production:

```bash
APP_FRONTEND_URL="https://darklift.app"
MAIL_FROM="DarkLift <noreply@darklift.app>"
```

## Frontend Integration

The frontend has two public routes:

- `/forgot-password`
- `/reset-password?token=...`

The reset page first asks for the 6-digit security code. After the code is entered, it shows the new password and confirmation fields.

The final reset request sends the token and code in the JSON body instead of an API query string. This keeps them out of common request-line logs.

The backend still requires CSRF headers for unsafe browser requests. The existing frontend HTTP helper calls `/api/v1/auth/csrf` and sends `X-XSRF-TOKEN` automatically.

## Security Notes

- Always use HTTPS in production.
- Do not log request bodies for `/api/v1/auth/reset-password`.
- Avoid analytics or third-party scripts on `/reset-password` that could read the URL token.
- Resend must verify `darklift.app` before `noreply@darklift.app` can reliably send mail.
