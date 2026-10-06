# Statut des user stories — gestion-location

Inventaire au **2026-10-06**.

## Totalement realises (v1)
US-01 a US-16, US-21 a US-29.
US-04 export CSV `/dashboard/series.csv`.
US-17 AES-GCM (`/crypto/roundtrip`, cle `app.crypto.key`).
US-18 agregateur branchable : `MOCK` par defaut, `app.psp.provider=WAVE` quand les cles existent. Bouton payer en ligne + webhook.
US-30 catalogue FR/EN/WO (menu + inscription).

Smoke : `sh scripts/api-smoke.sh` et `sh scripts/jalon-smoke.sh`.

## Ajouts v1.1 deja sur develop
- US-24 : verrou de reservation 24 h (`EN_ATTENTE`), `ACCEPTEE` bloque jusqu'au contrat. Libelle sur l'ecran Reservations.
- US-22 : `SIGNATURE_MODE=MOCK|LIVE|OFF`. Badge sur la liste des contrats. LIVE repond 501.
- Messages : `GET /api/v1/conversations/{id}/flux` et rafraichissement de l'ecran toutes les 4 s. Bus memoire, pas multi-replicas.
- Rate-limit login 30/min/IP, Vitest, baseline ZAP `sh scripts/zap-baseline.sh`.

Smoke v1.1 : `sh scripts/v11-smoke.sh`.

## Hors perimetre (cles prestataire)
- Wave / Orange Money live
- FCM (US-19)
- SMS (US-20) — email + cron deja en place
- Yousign / DocuSign live
- SSE multi-replicas
