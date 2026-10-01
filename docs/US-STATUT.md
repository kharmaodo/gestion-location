# Statut des user stories — gestion-location

Inventaire au **2026-10-01**.

## Realises
US-01 a US-16, US-21 a US-30.

- US-04 cartes, barres 6 mois, export CSV, accueil locataire
- US-17 2FA + AES-GCM
- US-18 paiement en ligne mock, journal PSP
- US-19 push FCM mock (`FCM_MODE`)
- US-20 email + cron ; SMS mock (`SMS_MODE`)
- US-30 FR/EN/WO menu et inscription

Smokes : `sh scripts/api-smoke.sh`, `sh scripts/jalon-smoke.sh`, `sh scripts/canaux-smoke.sh`.

## Reste, hors cles prestataire
Aucun ecran v1 ouvert.

## Reste, des que les cles existent
| US | Flag | A brancher |
|---|---|---|
| US-18 | `PSP_MODE=LIVE` | Wave, Orange Money, carte |
| US-19 | `FCM_MODE=LIVE` | Firebase |
| US-20 | `SMS_MODE=LIVE` | passerelle SMS |

`LIVE` repond 501 tant que le client HTTP n'est pas branche dans `CanalMockService`.
