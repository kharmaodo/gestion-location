#!/bin/sh
set -eu
API="${API:-http://localhost:8080}"
EMAIL="v11-$(date +%s)@example.com"
REG=$(curl -s -o /tmp/v11-reg.json -w "%{http_code}" -X POST "$API/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"motDePasse\":\"Secret123!\",\"nom\":\"V11\",\"prenom\":\"Smoke\",\"role\":\"PROPRIETAIRE\"}")
[ "$REG" = "201" ] || { echo "KO register $REG"; exit 1; }
TOKEN=$(curl -s -X POST "$API/api/v1/auth/login" -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"motDePasse\":\"Secret123!\"}" | python3 -c 'import json,sys; print(json.load(sys.stdin)["accessToken"])')
BODY=$(curl -s -H "Authorization: Bearer $TOKEN" "$API/api/v1/canaux")
echo "$BODY" | python3 -c 'import json,sys; d=json.load(sys.stdin); assert d.get("signature") in ("MOCK","LIVE","OFF"); print("OK signature", d.get("signature"))'
