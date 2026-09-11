# Flyway en Dev

**Ne jamais modifier une migration déjà appliquée** (`V1__identites.sql`). Toute évolution = `V2__...sql`.

## Checksum mismatch

```
Migration checksum mismatch for migration version 1
-> Applied to database : …
-> Resolved locally    : …
```

La base locale a enregistré un autre contenu de `V1` (fichier édité, fins de ligne, ou ancien brouillon).

### Correctif Dev (données jetables)

```bash
docker compose -f docker-compose.dev.yml down
docker volume ls | grep postgres
docker volume rm <volume_postgres_data>
docker compose -f docker-compose.dev.yml up -d postgres
cd backend && mvn spring-boot:run
```

Ou : `bash scripts/reset-dev-db.sh` puis relancer Maven.

### Interdit en Staging/Prod

Ne pas `repair` pour masquer un vrai écart de schéma. Restaurer le fichier historique ou ajouter une nouvelle version.
