# Statut des user stories — gestion-location

Inventaire au 2026-09-11, branche `feat/j14-paiement-en-ligne`.
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
| US-16 | Admin plateforme | Bootstrap 1er ADMIN, liste users, statut, rôles |
| US-21 | Quittance | Numéro généré à l’encaissement |
| US-24 | Réservation | Anti double-booking basique |
| US-25 | Publier / dépublier | |
| US-26 | Avis / notes | 1 avis / auteur / unité |
| US-27 | Messagerie interne | REST, pas WebSocket |
| US-28 | Litiges | Ouverture + décision proprio |

## Partiel

| US | Manque pour être « done » backlog |
|---|---|
| US-09 | URLs photo/vidéo ; pas d’upload S3 ni min 3 photos |
| US-10 | Chat interne ; pas de révélation des coordonnées après signature |
| US-11 | Résiliation ; pas de pénalité calculée |
| US-12 | Champ caution ; pas de simulateur ni plafond 3 mois |
| US-13 | EDL ENTREE/SORTIE ; pas de règle caution vs réparations |
| US-18 | Intention WAVE/OM/CARTE + webhook **mock** ; pas d’agrégateur réel |
| US-20 | Relances persistées ; pas d’email/SMS réel ni cron |
| US-23 | Docs KYC ; pas d’espace documentaire global |

## Non fait (priorité naturelle suivante)

| Prio | US | Titre |
|---|---|---|
| 1 | US-19 | Notification temps réel d’un paiement |
| 2 | US-22 | Signature électronique du contrat |
| 3 | US-17 | Chiffrement CNI / PII au repos |
| 4 | US-29 | Supervision ops |
| 5 | US-30 | i18n |

## Hors backlog initial mais livré

- Auth JWT sans Keycloak, refresh tokens
- Smoke `scripts/api-smoke.sh`
- Dashboard KPI accueil
- Bootstrap admin
- Mock checkout `/api/v1/public/paiements/webhook`
