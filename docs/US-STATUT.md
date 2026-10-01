# Statut des user stories — gestion-location

Inventaire au **2026-10-01**.

## Totalement realises (v1)
US-01 auth JWT, US-02 biens/unites, US-03 publication + photos, US-05 visites, US-06 vitrine/reservations, US-07 avis, US-08 dossier + KYC, US-09 medias MinIO, US-10 contrat, US-11 activation/occupation, US-12 caution, US-13 etat des lieux, US-14 litiges, US-15 loyers/quittances, US-16 messages, US-21 a US-28 (PDF, galerie, penalite, dossier depuis reservation, filtres listes, accueil locataire).

US-29 supervision : cron 08:30 + notification LOYER_RETARD.

## Partiel / prevu v1
| US | Fait | Reste |
|---|---|---|
| US-04 | Cartes + barres 6 mois, accueil locataire | Export graphes |
| US-17 | 2FA TOTP | Compte chiffrement avance |
| US-18 | Intention + webhook mock + bouton Payer en ligne | Agregateur reel |
| US-30 | Menu + page inscription FR/EN/WO | Autres formulaires |

## Hors perimetre v1 (prestataire)
- US-18 Wave / Orange Money / carte reels
- US-19 push FCM
- US-20 SMS (email Mailpit + cron relances deja en place)
