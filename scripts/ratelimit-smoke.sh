#!/bin/sh
set -eu
API="${API:-http://localhost:8080}"
i=1
last=0
while [ "$i" -le 31 ]; do
  last=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$API/api/v1/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"identifiant\":\"absent@test.sn\",\"motDePasse\":\"mauvais\"}")
  i=$((i + 1))
done
[ "$last" = "429" ] || { echo "KO rate-limit (attendu 429, obtenu $last)"; exit 1; }
echo "OK login 429"
