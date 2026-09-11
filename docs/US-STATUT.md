# Statut des user stories — gestion-location

Inventaire au 2026-09-11, branche `develop` + J12 médias.
Légende : **fait** = API + smoke ; **partiel** = socle présent, critères backlog incomplets ; **non fait**.

## Fait

| US | Titre | Couverture actuelle |
|---|---|---|
| US-01 | Liste locataires | CRUD dossiers, KYC |
| US-02 | Liste biens / unités | CRUD, statuts LIBRE/OCCUPE |
| US-03 | Suivi paiements | Échéances, partiel/payé |
| US-04 | Dashboard financier | Occupation, encaissé, à recouvrer |
| US-05 | Périodicité | JOURNALIER/HEBDO/MENSUEL + avenant |
| US-06 | Infos locataire | Email, tél, pièce, consentement |
| US-07 | Fiche bien publique | Vitrine filtrable |
| US-08 | RDV visite | Demande publique + confirmation |
| US-14 | Inscription / login | JWT |
| US-15 | Reset MDP + 2FA TOTP | |
| US-21 | Quittance | Numéro généré à l’encaissement |
| US-24 | Réservation | Anti double-booking basique |
| US-25 | Publier / dépublier | |
| US-26 | Avis / notes | 1 avis / auteur / unité |
| US-27 | Messagerie interne | REST, pas WebSocket |
| US-28 | Litiges | Ouverture + décision proprio |

## Partiel

| US | Manque pour être « done » backlog |
|---|---|
| US-09 | Photos/vidéos : J12 ajoute des **URLs** ; pas d’upload S3, pas de min 3 photos à la publication |
| US-10 | Chat interne oui ; pas de révélation des coordonnées après signature |
| US-11 | Résiliation contrat oui ; pas de pénalité calculée / affichée à la signature |
| US-12 | Champ caution oui ; pas de simulateur ni plafond 3 mois |
| US-13 | État des lieux ENTREE/SORTIE oui ; pas de workflow caution vs réparations ni photos horodatées |
| US-20 | Relances persistées oui ; pas d’email/SMS/WhatsApp réel ni job planifié |
| US-23 | Docs KYC dossier oui ; pas d’espace documentaire contrat/quittance/EDL |

## Non fait

| US | Titre |
|---|---|
| US-16 | Rôle administrateur plateforme + permissions |
| US-17 | Chiffrement CNI / PII au repos |
| US-18 | Paiement en ligne Mobile Money / carte |
| US-19 | Notification temps réel d’un paiement |
| US-22 | Signature électronique du contrat |
| US-29 | Supervision ops (jobs, paiements en échec) |
| US-30 | i18n (FR + autre langue) |

## Hors backlog initial mais livré

- Auth JWT sans Keycloak, refresh tokens
- Smoke `scripts/api-smoke.sh` (curl bout-en-bout)
- Dashboard KPI accueil

## Prochaines étapes suggérées

1. US-09 upload réel (MinIO) + contrainte 3 photos — *J12 = URLs seulement*
2. US-16 admin
3. US-18 paiement en ligne (agrégateur)
4. US-22 signature électronique
5. US-17 chiffrement PII
