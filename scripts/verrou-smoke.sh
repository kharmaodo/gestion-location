#!/bin/sh
set -eu
API="${API:-http://localhost:8080}"
STAMP=$(date +%s)
EMAIL="verrou.${STAMP}@test.sn"
PWD="Motdepasse1"
DEBUT=$(date +%F)
FIN=$(date -d "+10 days" +%F 2>/dev/null || date -v+10d +%F)

reg=$(curl -s -o /tmp/verrou-reg.json -w "%{http_code}" -X POST "$API/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"typeCompte\":\"PROPRIETAIRE\",\"email\":\"$EMAIL\",\"motDePasse\":\"$PWD\",\"prenom\":\"Awa\",\"nom\":\"Fall\",\"consentementRgpd\":true}")
[ "$reg" = "201" ] || { echo "KO register $reg"; exit 1; }
TOKEN=$(curl -s -X POST "$API/api/v1/auth/login" -H "Content-Type: application/json" \
  -d "{\"identifiant\":\"$EMAIL\",\"motDePasse\":\"$PWD\"}" | python3 -c 'import json,sys; print(json.load(sys.stdin)["accessToken"])')
BIEN=$(curl -s -X POST "$API/api/v1/biens" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"designation\":\"Villa verrou\",\"type\":\"MAISON\",\"ville\":\"Dakar\",\"adresse\":\"Mermoz\"}")
BIEN_ID=$(printf '%s' "$BIEN" | python3 -c 'import json,sys; print(json.load(sys.stdin)["id"])')
UNITE=$(curl -s -X POST "$API/api/v1/biens/$BIEN_ID/unites" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"libelle\":\"Chambre 1\",\"type\":\"CHAMBRE_SDB\",\"loyer\":85000,\"meuble\":true,\"periodicite\":\"MENSUEL\",\"jourEcheance\":5}")
UNITE_ID=$(printf '%s' "$UNITE" | python3 -c 'import json,sys; print(json.load(sys.stdin)["id"])')
i=1
while [ "$i" -le 3 ]; do
  curl -s -o /dev/null -X POST "$API/api/v1/unites/$UNITE_ID/medias" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
    -d "{\"url\":\"https://example.com/p$i.jpg\",\"type\":\"PHOTO\"}"
  i=$((i + 1))
done
curl -s -o /dev/null -X PUT "$API/api/v1/biens/$BIEN_ID/unites/$UNITE_ID/publication" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"publie\":true}"
ONE=$(curl -s -o /tmp/verrou-1.json -w "%{http_code}" -X POST "$API/api/v1/public/reservations" -H "Content-Type: application/json" \
  -d "{\"uniteId\":\"$UNITE_ID\",\"nom\":\"Diop\",\"telephone\":\"770000000\",\"dateDebut\":\"$DEBUT\",\"dateFin\":\"$FIN\"}")
[ "$ONE" = "201" ] || { echo "KO premiere reservation $ONE"; exit 1; }
echo "OK premiere reservation"
TWO=$(curl -s -o /tmp/verrou-2.json -w "%{http_code}" -X POST "$API/api/v1/public/reservations" -H "Content-Type: application/json" \
  -d "{\"uniteId\":\"$UNITE_ID\",\"nom\":\"Ndiaye\",\"telephone\":\"770000001\",\"dateDebut\":\"$DEBUT\",\"dateFin\":\"$FIN\"}")
[ "$TWO" = "409" ] || { echo "KO seconde reservation (attendu 409, obtenu $TWO)"; cat /tmp/verrou-2.json; exit 1; }
echo "OK verrou 24h 409"
