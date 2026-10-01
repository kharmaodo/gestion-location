# Statut des user stories — gestion-location

Inventaire au **2026-10-01**.

## Totalement realises (v1)
US-01 a US-16, US-21 a US-29.
US-04 export CSV `/dashboard/series.csv`.
US-17 AES-GCM (`/crypto/roundtrip`, cle `app.crypto.key`).
US-18 agregateur branchable : `MOCK` par defaut, `app.psp.provider=WAVE` quand les cles existent. Bouton payer en ligne + webhook.
US-30 catalogue FR/EN/WO (menu + inscription).

Smoke : `sh scripts/api-smoke.sh` et `sh scripts/jalon-smoke.sh`.

## Hors perimetre v1 (cles prestataire)
- Wave / Orange Money live
- FCM (US-19)
- SMS (US-20) — email + cron deja en place
