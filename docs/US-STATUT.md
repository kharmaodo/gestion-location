# Statut des user stories — gestion-location

Inventaire au **2026-10-08**.

## Totalement realises (v1)
US-01 a US-16, US-21 a US-29.
US-04 export CSV `/dashboard/series/export`.
US-17 AES-GCM (`/crypto/roundtrip`, cle `app.crypto.key`).
US-18 agregateur branchable : `MOCK` par defaut, `app.psp.provider=WAVE` quand les cles existent. Bouton payer en ligne + webhook.
US-30 catalogue FR/EN/WO (menu + inscription).

Smoke : `sh scripts/api-smoke.sh` et `sh scripts/jalon-smoke.sh`.

## Ajouts v1.1 deja sur develop
- US-24 : verrou de reservation 24 h (`EN_ATTENTE`), `ACCEPTEE` bloque jusqu'au contrat. Passage `EXPIREE` a la lecture. Filtre Expirees.
- US-22 : `SIGNATURE_MODE=MOCK|LIVE|OFF`. Badge sur la liste des contrats. LIVE repond 501. Smoke lien invalide 404.
- Messages : `GET /api/v1/conversations/{id}/flux` et rafraichissement de l'ecran toutes les 4 s. Bus memoire, pas multi-replicas.
- Rate-limit login 30/min/IP, Vitest, baseline ZAP `sh scripts/zap-baseline.sh`.

Smoke v1.1 : `sh scripts/v11-smoke.sh`, `sh scripts/signature-smoke.sh`, `sh scripts/ratelimit-smoke.sh`.

## Tests unitaires
Une regle Mockito par US produit, plus Flyway Testcontainers et le rate-limit.

| Lot | Fichier | Regles |
| --- | --- | --- |
| US-01 a US-08 | `LotUs01a08Test` | email deja pris, mot de passe faux, publication sans 3 photos, visite non publiee, annonce invisible, KYC EN_ATTENTE refuse, contrat deja actif |
| US-09 a US-29 | `LotUs09a29Test` | type media, doublon ENTREE, decision litige, alerte partielle, relance payee ignoree, message hors participants, intention deja payee, avis doublon, notification d'autrui, contacts masques, certificat et resiliation hors ACTIF |
| Reste | `LotUsRestantsTest` | type de bien invalide, entete CSV, paiement PARTIEL, quittance notifiee, caution non restituee sans EDL sortie valide |

Deja couverts a part : US-12 caution, US-17 chiffrement, US-22 signature, US-24 verrou, US-30 i18n.

Frontend Vitest : CSV, flux, signature, filtre reservation. Les pages n'ont pas de test de composant.

## Hors perimetre (cles prestataire)
- Wave / Orange Money live
- FCM (US-19)
- SMS (US-20) — email + cron deja en place
- Yousign / DocuSign live
- SSE multi-replicas
