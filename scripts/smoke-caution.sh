#!/usr/bin/env bash
set -euo pipefail
API="${API:-http://localhost:8080}"
echo "== Simulateur caution"
BODY=$(curl -sS "$API/api/v1/public/caution?loyer=85000&periodicite=MENSUEL&mois=5")
echo "$BODY"
python3 -c "import json,sys; d=json.loads(sys.argv[1]); assert d['moisRetenus']==3; assert float(d['cautionCalculee'])==255000" "$BODY"
echo OK
