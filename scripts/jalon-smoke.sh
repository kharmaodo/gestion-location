#!/bin/sh
# Smoke du jalon partiel : export CSV, AES-GCM, agregateur, i18n.
set -eu
API="${API:-http://localhost:8080}"
STAMP=$(date +%s)
EMAIL="jalon.${STAMP}@test.sn"
PWD="Motdepasse1"
FAIL=0

expect() {
  code="$1"; want="$2"; label="$3"
  case "$code" in
    "$want"*) echo "OK  $label ($code)" ;;
    *) echo "KO  $label (attendu $want, obtenu $code)"; FAIL=$((FAIL + 1)) ;;
  esac
}

code_of() { printf '%s\n' "$1" | tail -n 1; }
body_of() { printf '%s\n' "$1" | sed '$d'; }

OUT=$(curl -sS -w "\n%{http_code}" -X POST "$API/api/v1/auth/register" -H "Content-Type: application/json" \
  -d "{\"typeCompte\":\"PROPRIETAIRE\",\"email\":\"$EMAIL\",\"motDePasse\":\"$PWD\",\"prenom\":\"Awa\",\"nom\":\"Fall\",\"consentementRgpd\":true}")
expect "$(code_of "$OUT")" 201 "register"
OUT=$(curl -sS -w "\n%{http_code}" -X POST "$API/api/v1/auth/login" -H "Content-Type: application/json" \
  -d "{\"identifiant\":\"$EMAIL\",\"motDePasse\":\"$PWD\"}")
expect "$(code_of "$OUT")" 200 "login"
TOKEN=$(body_of "$OUT" | python3 -c "import json,sys; print(json.load(sys.stdin).get('accessToken',''))")

echo "== US-04 export graphes"
OUT=$(curl -sS -w "\n%{http_code}" -H "Authorization: Bearer $TOKEN" "$API/api/v1/dashboard/series.csv")
expect "$(code_of "$OUT")" 200 "GET /dashboard/series.csv"
printf '%s\n' "$(body_of "$OUT")" | head -n 1 | grep -q "mois,du,encaisse" && echo "OK  entete CSV" || { echo "KO  entete CSV"; FAIL=$((FAIL + 1)); }

echo "== US-17 chiffrement"
OUT=$(curl -sS -w "\n%{http_code}" -X POST "$API/api/v1/crypto/roundtrip" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"plain":"CNI-123"}')
expect "$(code_of "$OUT")" 200 "POST /crypto/roundtrip"
body_of "$OUT" | python3 -c "import json,sys; d=json.load(sys.stdin); assert d.get('plain')=='CNI-123' and d.get('stored','').startswith('enc:'); print('OK  AES-GCM')" || { echo "KO  AES-GCM"; FAIL=$((FAIL + 1)); }

echo "== US-18 agregateur"
OUT=$(curl -sS -w "\n%{http_code}" -H "Authorization: Bearer $TOKEN" "$API/api/v1/loyers/agregateur")
expect "$(code_of "$OUT")" 200 "GET /loyers/agregateur"
body_of "$OUT" | grep -q MOCK && echo "OK  provider MOCK" || { echo "KO  provider"; FAIL=$((FAIL + 1)); }

echo "== US-30 i18n"
OUT=$(curl -sS -w "\n%{http_code}" "$API/api/v1/public/i18n?lang=en")
expect "$(code_of "$OUT")" 200 "GET /public/i18n"
body_of "$OUT" | grep -q "Create an account" && echo "OK  cle EN" || { echo "KO  cle EN"; FAIL=$((FAIL + 1)); }

echo
if [ "$FAIL" -eq 0 ]; then echo "JALON SMOKE OK"; exit 0; fi
echo "$FAIL echec(s)"
exit 1
