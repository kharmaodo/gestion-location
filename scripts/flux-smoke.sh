#!/bin/sh
set -eu
API="${API:-http://localhost:8080}"
STAMP=$(date +%s)
PWD="Motdepasse1"
A="flux.a.${STAMP}@test.sn"
B="flux.b.${STAMP}@test.sn"

reg() {
  curl -s -o /tmp/flux-reg.json -w "%{http_code}" -X POST "$API/api/v1/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"typeCompte\":\"$1\",\"email\":\"$2\",\"motDePasse\":\"$PWD\",\"prenom\":\"Awa\",\"nom\":\"Fall\",\"consentementRgpd\":true}"
}
[ "$(reg PROPRIETAIRE "$A")" = "201" ] || { echo "KO register A"; exit 1; }
[ "$(reg LOCATAIRE "$B")" = "201" ] || { echo "KO register B"; exit 1; }
BID=$(python3 -c 'import json; print(json.load(open("/tmp/flux-reg.json")).get("userId",""))')
TOKEN=$(curl -s -X POST "$API/api/v1/auth/login" -H "Content-Type: application/json" \
  -d "{\"identifiant\":\"$A\",\"motDePasse\":\"$PWD\"}" | python3 -c 'import json,sys; print(json.load(sys.stdin)["accessToken"])')
CONV=$(curl -s -X POST "$API/api/v1/conversations" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"destinataireId\":\"$BID\"}")
CID=$(printf '%s' "$CONV" | python3 -c 'import json,sys; print(json.load(sys.stdin)["id"])')
curl -s -o /dev/null -X POST "$API/api/v1/conversations/$CID/messages" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"corps\":\"bonjour-flux\"}"
FLUX=$(curl -s -H "Authorization: Bearer $TOKEN" "$API/api/v1/conversations/$CID/flux")
printf '%s' "$FLUX" | python3 -c 'import json,sys; d=json.load(sys.stdin); assert "bonjour-flux" in d; print("OK flux")'
