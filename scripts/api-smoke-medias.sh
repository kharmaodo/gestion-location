#!/usr/bin/env bash
# À lancer après avoir une unité publiée (variables UNITE_ID + TOKEN).
set -euo pipefail
API="${API:-http://localhost:8080}"
curl -sS -o /tmp/m.json -w "%{http_code}" -X POST "$API/api/v1/unites/${UNITE_ID}/medias" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/photo.jpg","type":"PHOTO"}'
echo
curl -sS -w "\n%{http_code}\n" "$API/api/v1/public/annonces/${UNITE_ID}/medias"
