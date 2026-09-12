#!/usr/bin/env bash
set -euo pipefail
API="${API:-http://localhost:8080}"
echo "== Simulateur caution"
OUT=$(curl -sS -w "\n%{http_code}" "$API/api/v1/public/caution?loyer=85000&periodicite=MENSUEL&mois=5")
BODY=$(echo "$OUT" | sed '$d')
CODE=$(echo "$OUT" | tail -n1)
echo "HTTP $CODE $BODY"
[[ "$CODE" == 200 ]] || { echo "KO HTTP $CODE"; exit 1; }
python3 -c "import json,sys; d=json.loads(sys.argv[1]); assert int(float(d['moisRetenus']))==3; assert float(d['cautionCalculee'])==255000, d" "$BODY"
echo OK
