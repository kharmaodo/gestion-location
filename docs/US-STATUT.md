# Statut des user stories — gestion-location

Inventaire au 2026-09-11, `develop` + `feat/j17-pii`.

**Merges vérifiés sur `develop` :** J0–J14 + J16 (signature). **J15 notifications (#23) n’est pas sur `develop`.**

Légende : **fait** / **partiel** / **non fait**.

## Fait

| US | Titre |
|---|---|
| US-01 à US-08 | Locataires, biens, paiements, dashboard, périodicité, fiche, visites |
| US-14, US-15, US-16 | Auth JWT, 2FA, admin |
| US-21, US-24, US-25, US-26, US-27, US-28 | Quittance, résa, publication, avis, messages, litiges |

## Partiel

| US | Manque |
|---|---|
| US-09 | Upload S3, min 3 photos |
| US-10 | Révélation coordonnées après signature |
| US-11 | Pénalité de résiliation |
| US-12 | Simulateur caution 3 mois |
| US-13 | Caution vs réparations |
| US-17 | AES-GCM sur `piece_numero` ; email/tél encore en clair ; clé à changer en prod |
| US-18 | Mock Wave/OM/carte |
| US-19 | Inbox sur #23, pas mergée |
| US-20 | Relances sans canal réel |
| US-22 | Lien de signature, pas de certificat |
| US-23 | Pas d’espace docs global |

## Non fait

| Prio | US | Titre |
|---|---|---|
| 1 | US-29 | Supervision ops |
| 2 | US-30 | i18n |
