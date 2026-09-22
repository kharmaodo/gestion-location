# Statut des user stories — gestion-location

Inventaire au **2026-09-22** sur `develop` + PR galerie / US-11.

Légende : **Fait** = API + UI · **Partiel** = cœur livré · **Reste** = hors v1.

---

## Synthèse première release

| Lot | US | Statut |
|---|---|---|
| Auth / rôles | US-14, US-15, US-16 | Fait |
| Biens / locataires | US-01, US-02, US-06 | Fait (export CSV non fait) |
| Vitrine / visite / résa | US-07, US-08, US-24, US-25 | Fait |
| Médias | US-09 | Fait (upload MinIO + galerie annonce) |
| Contacts / chat | US-10, US-27 | Fait |
| Contrats / signatures | US-22 | Partiel (signature UI, attestation JSON pas PDF) |
| Caution | US-12, US-13 | Fait |
| Résiliation | US-11 | Fait (règle + préavis + pénalité API à l'écran) |
| Loyers | US-03, US-05, US-21 | Fait |
| Paiement en ligne | US-18 | Partiel (mock) |
| Relances / notifs | US-19, US-20 | Partiel (email + SSE ; pas SMS/FCM/cron) |
| Documents | US-23 | Fait |
| Avis / litiges | US-26, US-28 | Fait |
| Dashboard | US-04, US-29 | Partiel |
| PII | US-17 | Partiel |
| i18n | US-30 | Partiel |
| Lien résa → dossier | — | Fait (PR #78, nouveaux contrats) |

---

## Fait

US-01, 02, 03, 05, 06, 07, 08, **09**, 10, **11**, 12, 13, 14, 15, 16, 21, 23, 24, 25, 26, 27, 28.

Détail récent :
- US-09 : 3 photos min, upload MinIO, galerie sur `/annonces/:id`
- US-11 : bloc résiliation (préavis, estimation, motif API)
- Résa acceptée → contrat + dossier auto + `utilisateur_id`

---

## Partiel

| US | Manque |
|---|---|
| US-04 | Graphes |
| US-17 | Chiffrement compte utilisateur |
| US-18 | Agrégateur réel |
| US-19 | FCM |
| US-20 | Cron + SMS |
| US-22 | PDF attestation |
| US-29 | Cron supervision |
| US-30 | Textes formulaires |

---

## Après v1

Agrégateur Wave/OM, FCM, SMS, Yousign, app mobile, CSV locataires, carte géo, vidéo.

## Suite v1

1. US-22 PDF attestation
2. US-20 cron relances
