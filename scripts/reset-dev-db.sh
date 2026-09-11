#!/usr/bin/env bash
set -euo pipefail
# J0 / Dev only : détruit le volume Postgres et relance Flyway à zéro.
# À utiliser quand Flyway signale un checksum mismatch sur V1.
root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root"
docker compose -f docker-compose.dev.yml stop backend 2>/dev/null || true
docker compose -f docker-compose.dev.yml rm -f postgres 2>/dev/null || true
docker volume rm gestion-location_postgres_data 2>/dev/null || docker volume rm artifacts_postgres_data 2>/dev/null || true
docker compose -f docker-compose.dev.yml up -d postgres
echo "Postgres recréé. Relancer : cd backend && mvn spring-boot:run"
