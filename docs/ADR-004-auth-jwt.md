# ADR-004 — Authentification JWT applicative (sans Keycloak)

**Statut** : accepté  
**Date** : 2026-09-11  
**Jalon** : J0

## Décision

L'identité est gérée par l'application (`module identites`) avec Spring Security et des JWT signés HS256 (HS512 en prod recommandé). Keycloak et OIDC sont retirés du socle.

## Conséquences

- Plus de service Keycloak dans Docker Compose ni en production.
- Access token court (15 min) + refresh token opaque stocké hashé en base, révocable.
- Rôles métier `PROPRIETAIRE`, `LOCATAIRE`, `ADMIN` dans `identites.utilisateur_role`.
- 2FA TOTP / OTP SMS : prévu au J0 (schéma + endpoints), implémentation progressive.
- Le frontend stocke l'access token en mémoire et le refresh token en cookie httpOnly (dev : sessionStorage, à durcir).

## Alternatives rejetées

- Keycloak / OIDC : trop lourd pour le MVP et le mode d'exploitation souhaité.
- Sessions serveur uniquement : moins adapté au SPA + mobile.
