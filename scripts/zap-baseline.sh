#!/bin/sh
set -eu
API="${API:-http://host.docker.internal:8080}"
docker run --rm -t ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t "$API/actuator/health" -I || true
echo "Baseline ZAP terminee. Les alertes sont a lire, le script ne bloque pas la PR."
