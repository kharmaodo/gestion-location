#!/bin/sh
set -eu
API="${API:-http://localhost:8080}"
CODE=$(curl -s -o /tmp/signature-invalide.json -w "%{http_code}" -X POST "$API/api/v1/public/signatures/token-inconnu")
[ "$CODE" = "404" ] || { echo "KO lien invalide (attendu 404, obtenu $CODE)"; cat /tmp/signature-invalide.json; exit 1; }
echo "OK lien de signature invalide 404"
