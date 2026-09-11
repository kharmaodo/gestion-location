# J0 — Auth JWT

## Endpoints

| Méthode | Chemin | Accès |
|---|---|---|
| POST | `/api/v1/auth/register` | public |
| POST | `/api/v1/auth/login` | public |
| POST | `/api/v1/auth/refresh` | public (refresh opaque) |
| POST | `/api/v1/auth/logout` | public (révocation refresh) |
| GET | `/api/v1/me` | JWT |

## Tokens

- Access JWT, 15 min, claims `uid` + `roles`.
- Refresh opaque, 7 jours, hash SHA-256 en base, rotation à chaque refresh.

## Hors de ce commit (prochaine branche)

- Reset mot de passe email (MailHog déjà au compose).
- 2FA TOTP (colonnes déjà en schéma).
- Admin rôles UI.
