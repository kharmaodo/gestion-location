#!/bin/sh
set -eu
API="${API:-http://localhost:8080}"
STAMP=$(date +%s)
EMAIL="canaux.${STAMP}@test.sn"
PWD="Motdepasse1"
FAIL=0

expect() {
  case "$1" in
    "$2"*) echo "OK  $3 ($1)" ;;
    *) echo "KO  $3 (attendu $2, obtenu $1)"; FAIL=$((FAIL + 1)) ;;
  esac
}
code_of() { printf '%s\n' "$1" | tail -n 1; }
body_of() { printf '%s\n' "$1" | sed '$d'; }

OUT=$(curl -sS -w "\n%{http_code}" -X POST "$API/api/v1/auth/register" -H "Content-Type: application/json" \
  -d "{\"typeCompte\":\"PROPRIETAIRE\",\"email\":\"$EMAIL\",\"motDePasse\":\"$PWD\",\"prenom\":\"Awa\",\"nom\":\"Fall\",\"consentementRgpd\":true}")
expect "$(code_of "$OUT")" 201 "register"
OUT=$(curl -sS -w "\n%{http_code}" -X POST "$API/api/v1/auth/login" -H "Content-Type: application/json" \
  -d "{\"identifiant\":\"$EMAIL\",\"motDePasse\":\"$PWD\"}")
TOKEN=$(body_of "$OUT" | python3 -c "import json,sys; print(json.load(sys.stdin).get('accessToken',''))")

OUT=$(curl -sS -w "\n%{http_code}" -H "Authorization: Bearer $TOKEN" "$API/api/v1/canaux")
expect "$(code_of "$OUT")" 200 "GET /canaux"
body_of "$OUT" | grep -q '"sms":"MOCK"' && echo "OK  flag SMS" || { echo "KO  flag SMS"; FAIL=$((FAIL + 1)); }

OUT=$(curl -sS -w "\n%{http_code}" -X POST "$API/api/v1/canaux/sms" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"telephone":"770000000","message":"loyer"}')
expect "$(code_of "$OUT")" 200 "POST /canaux/sms"
body_of "$OUT" | grep -q MOCK && echo "OK  sms mock" || { echo "KO  sms mock"; FAIL=$((FAIL + 1)); }

OUT=$(curl -sS -w "\n%{http_code}" -X POST "$API/api/v1/canaux/push" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"token":"fcm-test","message":"alerte"}')
expect "$(code_of "$OUT")" 200 "POST /canaux/push"

OUT=$(curl -sS -w "\n%{http_code}" -X POST "$API/api/v1/canaux/psp" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"fournisseur":"WAVE","message":"85000"}')
expect "$(code_of "$OUT")" 200 "POST /canaux/psp"

echo
if [ "$FAIL" -eq 0 ]; then echo "CANAUX SMOKE OK"; exit 0; fi
echo "$FAIL echec(s)"
exit 1
