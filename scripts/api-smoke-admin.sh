#!/usr/bin/env bash
# Après login proprio : TOKEN + EMAIL + PWD.
set -euo pipefail
API="${API:-http://localhost:8080}"
BOOT="${ADMIN_BOOTSTRAP_TOKEN:-dev-admin-bootstrap}"
echo "bootstrap"
curl -sS -w "\n%{http_code}\n" -X POST "$API/api/v1/admin/bootstrap" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"token\":\"$BOOT\"}"
echo "re-login pour rafraichir les roles JWT"
BODY=$(curl -sS -X POST "$API/api/v1/auth/login" -H "Content-Type: application/json" \
  -d "{\"identifiant\":\"$EMAIL\",\"motDePasse\":\"$PWD\"}")
ADMIN_TOKEN=$(echo "$BODY" | python3 -c "import json,sys; print(json.load(sys.stdin).get('accessToken',''))")
curl -sS -w "\n%{http_code}\n" "$API/api/v1/admin/utilisateurs" -H "Authorization: Bearer $ADMIN_TOKEN"
