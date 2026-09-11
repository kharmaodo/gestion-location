# Gestion Location

Plateforme de gestion locative modulaire pour propriétaires, locataires et prospects.

## Stack validée

- Backend : Java 21, Spring Boot 3.3, Spring MVC, Spring Data JPA, Spring Security
- Authentification : **JWT applicatif** (access + refresh), sans Keycloak
- Base de données : PostgreSQL 16 + Flyway
- Frontend : React 18, TypeScript, TailwindCSS, Vite
- Événements : Kafka en production, Redpanda en développement
- Cache et temps réel : Redis et WebSocket/STOMP
- Recherche : Meilisearch
- Stockage : S3 en production, MinIO en développement

## Démarrage Dev

```bash
cp .env.example .env
docker compose -f docker-compose.dev.yml up -d postgres redis
cd backend && ./mvnw spring-boot:run
cd frontend-web && npm install && npm run dev
```

API : http://localhost:8080  
Web : http://localhost:5173  
Swagger : http://localhost:8080/swagger-ui.html

## Workflow Git

Les features vivent sur `feat/<jalon>-<sujet>`.  
`develop` n'est mis à jour qu'après validation explicite (PR).  
Ne pas pousser directement sur `develop` / `main`.

## Organisation

```text
gestion-location/
├── backend/
├── frontend-web/
├── docs/
├── backlog/
├── design/
└── docker-compose.dev.yml
```
