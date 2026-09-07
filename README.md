# Gestion Location

Plateforme de gestion locative modulaire pour propriétaires, locataires et prospects.

## Stack validée

- Backend : Java 21, Spring Boot 3, Spring MVC et Spring Data JPA
- Base de données : PostgreSQL
- Authentification : Keycloak avec OIDC/OAuth2
- Frontend : React, TypeScript, TailwindCSS et Vite
- Événements : Kafka en production, Redpanda en développement
- Cache et temps réel : Redis et WebSocket/STOMP
- Recherche : Meilisearch
- Stockage : S3 en production, MinIO en développement

## Coordonnées Maven

```text
io.github.kharmaodo:gestion-location-backend:0.1.0-SNAPSHOT
```

## Organisation cible

```text
gestion-location/
├── backend/
├── frontend-web/
├── mobile/
├── contracts/
├── infrastructure/
└── docs/
```

Le backend démarre sous forme de monolithe modulaire organisé par bounded contexts, sans Spring Modulith.
