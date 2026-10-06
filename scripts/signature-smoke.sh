#!/bin/sh
set -eu
API="${API:-http://localhost:8080}"
URL="$API/api/v1/public/signatures/token-inconnu"
printf 'POST %s\n' "$URL"
CODE=$(curl -sS -o /tmp/signature-invalide.json -w "%{http_code}" -X POST "$URL" || true)
printf 'HTTP %s\n' "$CODE"
if [ "$CODE" = "404" ]; then
  echo "OK lien de signature invalide"
  exit 0
fi
echo "KO lien invalide (attendu 404)"
cat /tmp/signature-invalide.json
exit 1
