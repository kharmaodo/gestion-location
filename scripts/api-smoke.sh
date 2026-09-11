#!/usr/bin/env bash
# Smoke tests HTTP des APIs (J0 → J9).
set -euo pipefail

API="${API:-http://localhost:8080}"
STAMP=$(date +%s)
PRO_EMAIL="proprio.smoke.${STAMP}@test.sn"
LOC_EMAIL="locataire.smoke.${STAMP}@test.sn"
PWD="Motdepasse1"
FAIL=0

expect() {
  local code="$1" want="$2" label="$3"
  if [[ "$code" == "$want" || "$code" == "$want"* ]]; then
    echo "OK  $label ($code)"
  else
    echo "KO  $label (attendu $want, obtenu $code)"
    FAIL=$((FAIL+1))
  fi
}

req() {
  local method="$1" path="$2" data="${3:-}" token="${4:-}"
  local args=(-sS -w "\n%{http_code}" -X "$method" "$API$path" -H "Content-Type: application/json")
  if [[ -n "$token" ]]; then args+=(-H "Authorization: Bearer $token"); fi
  if [[ -n "$data" ]]; then args+=(-d "$data"); fi
  curl "${args[@]}"
}

split_body_code() {
  BODY=$(echo "$1" | sed '$d')
  CODE=$(echo "$1" | tail -n1)
}

echo "== Health"
OUT=$(req GET /actuator/health)
split_body_code "$OUT"
expect "$CODE" 200 "GET /actuator/health"

echo "== Register proprietaire"
OUT=$(req POST /api/v1/auth/register "{\"typeCompte\":\"PROPRIETAIRE\",\"email\":\"$PRO_EMAIL\",\"motDePasse\":\"$PWD\",\"prenom\":\"Awa\",\"nom\":\"Fall\",\"consentementRgpd\":true}")
split_body_code "$OUT"
expect "$CODE" 201 "POST /auth/register proprio"

echo "== Login proprio"
OUT=$(req POST /api/v1/auth/login "{\"identifiant\":\"$PRO_EMAIL\",\"motDePasse\":\"$PWD\"}")
split_body_code "$OUT"
expect "$CODE" 200 "POST /auth/login"
PRO_TOKEN=$(echo "$BODY" | python3 -c "import json,sys; print(json.load(sys.stdin).get('accessToken',''))")

echo "== Me"
OUT=$(req GET /api/v1/me "" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 200 "GET /me"

echo "== Register locataire"
OUT=$(req POST /api/v1/auth/register "{\"typeCompte\":\"LOCATAIRE\",\"email\":\"$LOC_EMAIL\",\"motDePasse\":\"$PWD\",\"prenom\":\"Ibra\",\"nom\":\"Diop\",\"consentementRgpd\":true}")
split_body_code "$OUT"
expect "$CODE" 201 "POST /auth/register locataire"
LOC_TOKEN=$(echo "$BODY" | python3 -c "import json,sys; print(json.load(sys.stdin).get('accessToken',''))")
LOC_ID=$(echo "$BODY" | python3 -c "import json,sys; print(json.load(sys.stdin).get('userId',''))")

echo "== Bien + unite"
OUT=$(req POST /api/v1/biens "{\"designation\":\"Villa smoke\",\"type\":\"MAISON\",\"ville\":\"Dakar\",\"adresse\":\"Mermoz\"}" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 201 "POST /biens"
BIEN_ID=$(echo "$BODY" | python3 -c "import json,sys; print(json.load(sys.stdin).get('id',''))")

OUT=$(req POST "/api/v1/biens/${BIEN_ID}/unites" "{\"libelle\":\"Chambre 1\",\"type\":\"CHAMBRE_SDB\",\"loyer\":85000,\"meuble\":true,\"periodicite\":\"MENSUEL\",\"jourEcheance\":5}" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 201 "POST /biens/{id}/unites"
UNITE_ID=$(echo "$BODY" | python3 -c "import json,sys; print(json.load(sys.stdin).get('id',''))")

OUT=$(req PUT "/api/v1/biens/${BIEN_ID}/unites/${UNITE_ID}/publication" "{\"publie\":true}" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 200 "PUT publication"

echo "== Visite (avant occupation)"
CRENEAU=$(python3 -c "from datetime import datetime,timedelta,timezone; print((datetime.now(timezone.utc)+timedelta(days=2)).strftime('%Y-%m-%dT%H:%M:%SZ'))")
OUT=$(req POST /api/v1/public/visites "{\"uniteId\":\"$UNITE_ID\",\"nom\":\"Ibra\",\"telephone\":\"770000000\",\"email\":\"$LOC_EMAIL\",\"creneau\":\"$CRENEAU\"}")
split_body_code "$OUT"
expect "$CODE" 201 "POST /public/visites"
VIS_ID=$(echo "$BODY" | python3 -c "import json,sys; print(json.load(sys.stdin).get('id',''))")

OUT=$(req GET /api/v1/visites "" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 200 "GET /visites"

if [[ -n "$VIS_ID" ]]; then
  OUT=$(req POST "/api/v1/visites/${VIS_ID}/statut" "{\"statut\":\"CONFIRMEE\"}" "$PRO_TOKEN")
  split_body_code "$OUT"
  expect "$CODE" 200 "POST /visites/{id}/statut"
fi

echo "== Vitrine publique"
OUT=$(req GET "/api/v1/public/annonces?ville=Dakar")
split_body_code "$OUT"
expect "$CODE" 200 "GET /public/annonces"

OUT=$(req GET "/api/v1/public/annonces/${UNITE_ID}")
split_body_code "$OUT"
expect "$CODE" 200 "GET /public/annonces/{id}"

DEBUT=$(date +%F)
FIN=$(date -d "+10 days" +%F 2>/dev/null || date -v+10d +%F)
OUT=$(req POST /api/v1/public/reservations "{\"uniteId\":\"$UNITE_ID\",\"nom\":\"Ibra\",\"telephone\":\"770000000\",\"dateDebut\":\"$DEBUT\",\"dateFin\":\"$FIN\"}")
split_body_code "$OUT"
expect "$CODE" 201 "POST /public/reservations"

echo "== Avis"
OUT=$(req POST /api/v1/avis "{\"cibleUniteId\":\"$UNITE_ID\",\"note\":5,\"commentaire\":\"Tres bien\"}" "$LOC_TOKEN")
split_body_code "$OUT"
expect "$CODE" 201 "POST /avis"
OUT=$(req GET "/api/v1/public/annonces/${UNITE_ID}/avis")
split_body_code "$OUT"
expect "$CODE" 200 "GET /public/annonces/{id}/avis"
OUT=$(req POST /api/v1/avis "{\"cibleUniteId\":\"$UNITE_ID\",\"note\":4}" "$LOC_TOKEN")
split_body_code "$OUT"
expect "$CODE" 409 "POST /avis doublon"

echo "== Dossier locataire"
OUT=$(req POST /api/v1/locataires "{\"nom\":\"Diop\",\"prenom\":\"Ibra\",\"telephone\":\"770000000\",\"email\":\"$LOC_EMAIL\",\"pieceType\":\"CNI\",\"pieceNumero\":\"123\"}" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 201 "POST /locataires"
DOS_ID=$(echo "$BODY" | python3 -c "import json,sys; print(json.load(sys.stdin).get('id',''))")

OUT=$(req POST "/api/v1/locataires/${DOS_ID}/kyc" "{\"statut\":\"VALIDE\",\"commentaire\":\"ok\"}" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 200 "POST /locataires/{id}/kyc"

echo "== Contrat"
OUT=$(req POST /api/v1/contrats "{\"uniteId\":\"$UNITE_ID\",\"dossierId\":\"$DOS_ID\",\"dateDebut\":\"$DEBUT\",\"loyer\":85000}" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 201 "POST /contrats"
CTR_ID=$(echo "$BODY" | python3 -c "import json,sys; print(json.load(sys.stdin).get('id',''))")

OUT=$(req POST "/api/v1/contrats/${CTR_ID}/activation" "" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 200 "POST /contrats/{id}/activation"

echo "== Loyers"
OUT=$(req POST /api/v1/loyers/generation "" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 200 "POST /loyers/generation"
ECH_ID=$(echo "$BODY" | python3 -c "import json,sys; d=json.load(sys.stdin); print(d[0]['id'] if d else '')")

if [[ -n "$ECH_ID" ]]; then
  OUT=$(req POST "/api/v1/loyers/${ECH_ID}/paiements" "{\"montant\":10000,\"mode\":\"ESPECES\"}" "$PRO_TOKEN")
  split_body_code "$OUT"
  expect "$CODE" 200 "POST /loyers/{id}/paiements partiel"
fi

OUT=$(req POST /api/v1/loyers/relances "" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 200 "POST /loyers/relances"

echo "== Dashboard + messages"
OUT=$(req GET /api/v1/dashboard "" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 200 "GET /dashboard"

OUT=$(req POST /api/v1/conversations "{\"destinataireId\":\"$LOC_ID\"}" "$PRO_TOKEN")
split_body_code "$OUT"
expect "$CODE" 201 "POST /conversations"
CONV_ID=$(echo "$BODY" | python3 -c "import json,sys; print(json.load(sys.stdin).get('id',''))")

if [[ -n "$CONV_ID" ]]; then
  OUT=$(req POST "/api/v1/conversations/${CONV_ID}/messages" "{\"corps\":\"Bonjour\"}" "$PRO_TOKEN")
  split_body_code "$OUT"
  expect "$CODE" 201 "POST /conversations/{id}/messages"
fi

echo "== Isolation locataire (ne doit pas creer un bien)"
OUT=$(req POST /api/v1/biens "{\"designation\":\"X\",\"type\":\"MAISON\"}" "$LOC_TOKEN")
split_body_code "$OUT"
expect "$CODE" 403 "POST /biens en tant que LOCATAIRE"

echo
if [[ "$FAIL" -eq 0 ]]; then
  echo "TOUS LES SMOKE TESTS SONT PASSÉS"
  exit 0
fi
echo "$FAIL test(s) en echec"
exit 1
