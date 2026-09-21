# Statut des user stories — gestion-location

Inventaire au **2026-09-21** sur `develop` (après PR #76).

Légende : **Fait** = API + UI utilisables · **Partiel** = cœur livré, manque un canal ou un livrable · **Reste** = hors périmètre v1 ou non commencé.

---

## Synthèse première release

| Lot | US | Statut |
|---|---|---|
| Auth / rôles | US-14, US-15, US-16 | Fait |
| Biens / locataires | US-01, US-02, US-06 | Fait (export CSV non fait) |
| Vitrine / visite / résa | US-07, US-08, US-24, US-25 | Fait |
| Médias | US-09 | Fait (upload MinIO, 3 photos min) — galerie publique à vérifier |
| Contacts / chat | US-10, US-27 | Fait |
| Contrats / signatures | US-22 | Partiel (invitation + signature, attestation JSON pas PDF) |
| Caution | US-12, US-13 | Fait (simulateur, plafond 3 mois, restitution vs EDL) |
| Résiliation | US-11 | Partiel (pénalité API, peu visible dans l’UI) |
| Loyers | US-03, US-05, US-21 | Fait (encaissement, génération, reçu) |
| Paiement en ligne | US-18 | Partiel (intention mock, pas d’agrégateur réel) |
| Relances / notifs | US-19, US-20 | Partiel (email Mailpit + inbox SSE ; pas SMS, FCM, cron) |
| Documents | US-23 | Fait (MinIO + meta URL) |
| Avis / litiges | US-26, US-28 | Fait |
| Dashboard | US-04, US-29 | Partiel (KPI + alertes ; pas de cron ni graphes riches) |
| PII | US-17 | Partiel (dossier chiffré ; compte utilisateur encore clair) |
| i18n | US-30 | Partiel (shell FR/EN/WO ; pages encore en dur FR) |

**Hors v1 (Won't)** : chatbot, matching, fidélité, marketplace annexes.

---

## Fait

| US | Contenu livré |
|---|---|
| US-01 | Liste locataires, recherche `?q=`, KYC valider/rejeter |
| US-02 | Biens + unités, statuts, détail |
| US-03 | Page `/loyers`, encaissement, historique reçus |
| US-05 | Périodicité à la création + avenant |
| US-06 | Dossier (tél, email, CNI) + documents |
| US-07 | Vitrine publique, filtres ville/type/loyer/`q` |
| US-08 | Demande de visite depuis l’annonce + confirmation proprio |
| US-09 | 3 photos min, upload fichier MinIO |
| US-10 | Contacts révélés après double signature |
| US-12 | API plafond + page `/caution` |
| US-13 | EDL entrée/sortie + restitution caution |
| US-14 | Register / login JWT |
| US-15 | Forgot/reset + 2FA |
| US-16 | Rôles PROPRIETAIRE / LOCATAIRE / isolation 403 |
| US-21 | Numéro de reçu à l’encaissement |
| US-23 | Espace documents dossier |
| US-24 | Réservation publique + décision + contrat depuis résa acceptée |
| US-25 | Publication / dépublication (garde 3 photos) |
| US-26 | Avis sur annonce |
| US-27 | Conversations / messages |
| US-28 | Litiges (liste, ouverture, décision) |

---

## Partiel (v1 si on tranche le minimum)

| US | Livré | Manque pour être « fini » |
|---|---|---|
| US-04 | Dashboard KPI + alertes | Graphes mensuels, seuil impayés paramétrable |
| US-11 | Calcul pénalité 1 loyer si préavis < 30 j | Affichage avant signature + UI résiliation locataire |
| US-17 | AES-GCM dossier | Chiffrement compte utilisateur + consentement tracé |
| US-18 | Intention paiement + webhook mock | Wave / OM / carte réels |
| US-19 | Inbox + SSE | Push FCM |
| US-20 | Relance email (Mailpit) bouton manuel | Cron J-X / J+X + SMS |
| US-22 | Invitation signature + bouton Signer + attestation JSON | PDF horodaté / prestataire e-sign |
| US-29 | Liste alertes accueil | Jobs supervision + cron |
| US-30 | Sélecteur langue + nav | Textes des formulaires |

### Dette produit liée (pas une US numérotée)

- Contrat depuis réservation : **pas de dossier** ni `utilisateur_id` → le locataire connecté ne voit pas toujours « ses » contrats.
- Export CSV locataires (critère US-01).
- Carte géolocalisée (critère US-07).
- Vidéo bien (critère US-09).
- Galerie zoomable sur la fiche **annonce** publique.

---

## Restant / après v1

| Sujet | Commentaire |
|---|---|
| US-18 agrégateur | PayDunya / CinetPay / Stripe |
| US-19 FCM | App mobile absente |
| US-20 SMS / WhatsApp | Twilio |
| US-22 PDF + Yousign/DocuSign | Attestation JSON suffit en interne |
| Admin plateforme | Persona admin peu exposée hors rôle |
| Frontend mobile | RN non démarré |

---

## Ordre proposé pour clôturer la v1

1. Lier réservation → dossier + `utilisateur_id` (contrats locataire).
2. US-11 : afficher la pénalité à la résiliation.
3. Photos sur la fiche annonce publique.
4. US-22 : PDF d’attestation simple (sans prestataire).
5. US-20 : job de relance planifié.
6. Mettre à jour ce fichier à chaque merge.
