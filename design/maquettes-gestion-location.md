# Conception UI/UX — Plateforme Gestion de Location
## Proposition d'écrans par jalon (J0 → J9)

> **Source de vérité** : `Backlog_Gestion_Location.md` (personas, US-01 à US-30, découpage en jalons J0-J9, arbitrages MVP section 8, architecture section 11).
> **Note d'accès** : le clone de `github.com/kharmaodo/gestion-location.git` (branche `develop`) est bloqué par la politique réseau de mon environnement. L'analyse s'appuie donc sur le backlog et la description de containerisation présents dans vos documents, dont le contenu correspond à ce qui est versionné dans `backlog/` et à la racine du dépôt. Si le `docker-compose.dev.yml` réel diverge de la section 7 du backlog, les impacts sont signalés en fin de document.

---

## 0. Socle commun — système de composants

Toutes les maquettes réutilisent le même système, cohérent avec la stack retenue (React + TypeScript + TailwindCSS via Vite, SPA sans SSR).

### 0.1 Grille & conteneurs
- Breakpoints Tailwind : `sm 640 / md 768 / lg 1024 / xl 1280`.
- Contenu applicatif : conteneur max `1280px`, gouttière `24px`, grille 12 colonnes.
- Pages publiques (annonces) : pleine largeur avec sections `max-w-7xl`.

### 0.2 Tokens
| Token | Usage |
|---|---|
| `primary` | Actions principales, liens actifs |
| `success` | Payé, contrat actif, bien libre |
| `warning` | Partiel, en attente de signature, échéance proche |
| `danger` | Impayé, litige, incident critique |
| `neutral-50..900` | Fonds, bordures, textes |
| Typo | 1 famille, échelle `xs / sm / base / lg / xl / 2xl / 3xl` |
| Rayon | `rounded-lg` cartes, `rounded-md` champs/boutons |

### 0.3 Composants transverses (à construire une fois, au J0/J1)
`AppShell` (sidebar + topbar + breadcrumb) · `DataTable` (tri, pagination serveur, filtres persistés en URL, export CSV) · `FilterBar` · `StatCard` · `StatusBadge` (mapping unique statut → couleur, partagé bien/contrat/paiement/incident) · `Drawer` (détail latéral) · `Modal` de confirmation · `Stepper` (parcours multi-étapes) · `FileUploader` (drag & drop, aperçu, barre de progression) · `MediaGallery` · `MapView` · `DateTimePicker` · `MoneyInput` (XOF, `numeric(19,2)`) · `EmptyState` · `Skeleton` · `ToastCenter` · `RealtimeBadge` (état de la connexion WebSocket) · `AuditTrailList`.

### 0.4 Règles UX transverses
- **3 espaces distincts** avec navigation propre : **Public** (prospect, non authentifié), **Propriétaire**, **Locataire**, plus **Admin** (back-office séparé). Cohérent avec la décision SPA multi-espaces de la section 11.1.
- **Temps réel** : tout écran alimenté par WebSocket affiche un `RealtimeBadge` et dégrade en polling silencieux si la connexion tombe — jamais d'écran figé sans indication.
- **Données sensibles (CNI)** : masquage par défaut (`•••• ••••`), révélation à la demande, chaque révélation tracée et affichée dans l'`AuditTrailList` de la fiche.
- **Accessibilité** : WCAG 2.1 AA sur les parcours essentiels (exigence non fonctionnelle §11.9) — contraste 4.5:1, focus visible, navigation clavier complète sur les tableaux et le stepper contrat.
- **États obligatoires** pour chaque écran : chargement (skeleton), vide (EmptyState orienté action), erreur (Problem Details de l'API), succès (toast).

---

## J0 — Fondations techniques & environnements
**Objectif** : socle d'authentification, de rôles et de conformité opérationnel sur les 3 environnements, prérequis bloquant de tous les autres jalons.

### Écrans

#### J0-E1 — Inscription
- **US** : US-14
- **Éléments** : choix du type de compte (Propriétaire / Locataire-Prospect), email **ou** téléphone, mot de passe avec jauge de robustesse, case de consentement RGPD reliée à une modale « Traitement de vos données », bouton `Créer mon compte`, lien vers connexion.
- **Actions** : soumettre, basculer email↔téléphone, lire la politique de confidentialité.
- **Note** : l'authentification est déléguée à Keycloak (OIDC). L'écran est soit la page Keycloak *themée* aux tokens ci-dessus, soit un écran applicatif relayant le flux — **le backlog ne tranche pas ce point ; à arbitrer en ADR-004**.

```
┌──────────────────────────────────────────┐
│  [Logo]            Déjà un compte ? [Se connecter] │
├──────────────────────────────────────────┤
│         Créer votre compte               │
│  ( ) Je suis propriétaire                │
│  ( ) Je cherche un logement              │
│                                          │
│  [ Email ▾ / Téléphone ]                 │
│  [_______________________________]       │
│  Mot de passe                            │
│  [_______________________]  ▇▇▇░░ Moyen  │
│                                          │
│  [x] J'accepte le traitement de mes      │
│      données personnelles  (voir détail) │
│                                          │
│  [        Créer mon compte        ]      │
└──────────────────────────────────────────┘
```

#### J0-E2 — Connexion + vérification OTP / 2FA
- **US** : US-14, US-15
- **Éléments** : identifiant, mot de passe, « mot de passe oublié », puis écran OTP à 6 cases avec compte à rebours de renvoi (60 s) et canal utilisé (SMS/email) ; option « se souvenir de cet appareil ».

```
┌──────────────────────────┐   ┌──────────────────────────────┐
│  Connexion               │ → │  Vérification                │
│  [identifiant_________]  │   │  Code envoyé au +221 •• •• 42│
│  [mot de passe________]  │   │  [_][_][_][_][_][_]          │
│  Mot de passe oublié ?   │   │  Renvoyer dans 00:47          │
│  [     Se connecter    ] │   │  [ ] Appareil de confiance    │
└──────────────────────────┘   └──────────────────────────────┘
```

#### J0-E3 — Réinitialisation de mot de passe (2 étapes)
- **US** : US-15
- **Éléments** : saisie identifiant → écran neutre « si ce compte existe, un lien a été envoyé » (anti-énumération) → formulaire nouveau mot de passe + confirmation, lien à usage unique expiré géré par un écran dédié.

#### J0-E4 — Paramètres de sécurité du compte
- **US** : US-15
- **Éléments** : activation/désactivation 2FA (TOTP ou SMS), liste des appareils de confiance avec révocation, historique des connexions (date, IP tronquée, appareil), changement de mot de passe.

#### J0-E5 — Consentements & confidentialité
- **US** : US-17
- **Éléments** : liste des consentements avec source et horodatage (table `identites.consentement`), bouton de retrait, demande d'export de mes données, demande de suppression/anonymisation.
- **Signalement** : le backlog impose la politique de conservation/export/suppression (§11.5) mais **ne définit pas les délais ni le workflow de validation d'une demande de suppression** — écran conçu en « demande soumise, traitement sous X », `X` à préciser.

#### J0-E6 — Admin : gestion des rôles & permissions
- **US** : US-16
- **Éléments** : `DataTable` utilisateurs (nom, email, rôles multiples, statut, date), filtres par rôle/statut, `Drawer` d'édition avec cases à cocher de rôles métier (propriétaire / locataire / admin / gestionnaire délégué), matrice rôles × permissions en lecture, justification obligatoire à chaque modification, journal d'audit.
- **Signalement** : le rôle **gestionnaire délégué** est cité comme « éventuel » en US-16 sans périmètre de permissions — la matrice affichera ce rôle en lecture seule tant qu'il n'est pas spécifié.

```
┌─ Admin ▸ Utilisateurs ───────────────────────────────────────┐
│ [🔎 recherche] [Rôle ▾][Statut ▾]            [Exporter CSV]  │
├───────────┬──────────────────┬───────────────┬──────────────┤
│ Nom       │ Contact          │ Rôles         │ Statut       │
├───────────┼──────────────────┼───────────────┼──────────────┤
│ A. Fall   │ a.fall@..        │ [Propriétaire]│ ● Actif      │
│ M. Ndiaye │ +221 •• •• 12    │ [Locataire]   │ ● Actif      │
└───────────┴──────────────────┴───────────────┴──────────────┘
       ↳ clic ligne → Drawer : rôles, permissions, audit
```

#### J0-E7 — États système transverses
- **US** : transverse (support de US-14 à US-17)
- **Éléments** : 403 (accès refusé au rôle courant), 404, session expirée avec re-login en modale sans perte du formulaire en cours, page de maintenance/incident.

---

## J1 — Back-office propriétaire (cœur métier)
**Objectif** : le propriétaire gère intégralement son parc, ses locataires et la périodicité de facturation.

### Écrans

#### J1-E1 — Accueil propriétaire (version J1)
- **US** : US-01, US-02
- **Éléments** : 4 `StatCard` (biens total, unités libres, unités occupées, locataires actifs), raccourcis « Ajouter un bien / Ajouter un locataire », liste des 5 dernières activités.
- **Note** : les indicateurs financiers (revenus, impayés) n'apparaissent qu'au J5 — cet écran est volontairement dégradé au J1 pour éviter des cartes vides.

#### J1-E2 — Liste des biens
- **US** : US-02
- **Éléments** : `DataTable`/vue cartes commutable, colonnes (photo, désignation, type, adresse, nb d'unités, occupées/libres, loyer de référence, statut), filtres type / adresse / fourchette de prix / statut (libre, occupé, en travaux), recherche, `StatusBadge` temps réel, actions par ligne (voir, éditer, ajouter une unité).

```
┌─ Mes biens ──────────────────────────────────────────────────┐
│ [🔎] [Type ▾][Ville ▾][Prix: ──●───●──][Statut ▾]  [+ Bien]  │
├────┬──────────────┬─────────┬──────────┬────────┬───────────┤
│ 🏠 │ Villa Sacré-C│ Maison  │ Dakar    │ 4 unités│ 3 occ/1 libre │
│ 🏢 │ Résid. Liberté│ Appart │ Dakar    │ 6 unités│ ● En travaux  │
└────┴──────────────┴─────────┴──────────┴────────┴───────────┘
```

#### J1-E3 — Création / édition d'un bien (Stepper)
- **US** : US-02
- **Étapes** : 1) Identité du bien (désignation, type) → 2) Adresse + géolocalisation (`MapView`, pin déplaçable) → 3) Unités locatives (ajout en ligne : libellé, type d'unité — chambre simple / chambre avec SDB / studio / appartement / maison —, surface, meublé oui/non, loyer XOF) → 4) Équipements (sélection multiple) → 5) Récapitulatif.
- **Composants clés** : `Stepper`, `MoneyInput` XOF, tableau d'unités éditable, sauvegarde brouillon à chaque étape.

```
┌ Nouveau bien ─ ①Identité ─ ②Adresse ─ ③Unités ─ ④Équip. ─ ⑤Récap ┐
│ ③ Unités locatives                                               │
│ ┌────────────┬────────────────┬────────┬────────┬─────────────┐  │
│ │ Libellé    │ Type           │ Surface│ Meublé │ Loyer (XOF) │  │
│ │ Chambre 1  │ Chambre + SDB ▾│  14 m² │ [x]    │   85 000    │  │
│ │ [+ ajouter une unité]                                       │  │
│ └────────────┴────────────────┴────────┴────────┴─────────────┘  │
│                                   [Précédent]  [Continuer →]     │
└──────────────────────────────────────────────────────────────────┘
```

#### J1-E4 — Détail d'un bien
- **US** : US-02, US-05
- **Éléments** : en-tête (photo principale, désignation, adresse, statut), onglets **Unités** (liste + statut + locataire courant) / **Équipements** / **Médias** (activé au J2) / **Périodicité & facturation** / **Historique**.

#### J1-E5 — Configuration de la périodicité de paiement
- **US** : US-05
- **Éléments** : définie **par unité locative** (le backlog dit « par bien », mais la propriété louable est l'unité — à confirmer avec le PO) : choix journalier / hebdomadaire / **mensuel (défaut)**, jour d'échéance, aperçu généré des 6 prochaines échéances, bannière bloquante « périodicité verrouillée : contrat signé — passer par un avenant » avec lien vers la création d'avenant (J4).

```
┌ Chambre 1 ▸ Facturation ──────────────────────────────┐
│ Périodicité   ( ) Journalier ( ) Hebdo (•) Mensuel    │
│ Jour d'échéance  [ 5 ] de chaque mois                 │
│ Loyer            [ 85 000 XOF ]                       │
│ ── Aperçu des échéances générées ───────────────────  │
│  05/10  85 000 · 05/11  85 000 · 05/12  85 000 …      │
│ ⚠ Contrat actif : modification via avenant uniquement │
└───────────────────────────────────────────────────────┘
```

#### J1-E6 — Liste des locataires
- **US** : US-01
- **Éléments** : `DataTable` (nom, téléphone, email, unité occupée, statut actif/résilié, date d'entrée), filtres **actif / résilié**, recherche par **nom ou téléphone**, **export CSV** (critère d'acceptation explicite US-01), action « Nouveau dossier locataire ».

#### J1-E7 — Dossier locataire (création / fiche)
- **US** : US-06, US-17
- **Éléments** : identité, email, téléphone (canaux de contact cochables : email, SMS, WhatsApp, Telegram, Signal), **CNI** (numéro masqué + upload recto/verso via `FileUploader`), bandeau « données chiffrées », capture du consentement RGPD obligatoire avec horodatage, onglets Contrats / Paiements / Documents / Incidents (grisés tant que le jalon correspondant n'est pas livré).
- **Signalement** : le module `locataires` est explicitement « à concevoir au J1, hors périmètre du draft SQL initial » (§9.4). Les champs exacts du dossier (situation professionnelle, garant, justificatifs de revenus ?) **ne sont pas spécifiés** — la maquette se limite strictement à ce qu'exige US-06.

```
┌ Nouveau dossier locataire ───────────────────────────────┐
│ Identité     [Prénom______] [Nom__________]              │
│ Contact      [email________] [téléphone_____]            │
│ Canaux de relance  [x]Email [x]SMS [ ]WhatsApp [ ]Telegram│
│ ── Pièce d'identité ──────────────────────────────────── │
│ N° CNI  [••••••••••]  👁 afficher (action tracée)        │
│ [ Déposer recto ]  [ Déposer verso ]      🔒 chiffré     │
│ ── Conformité ──────────────────────────────────────────  │
│ [x] Consentement collecté le 11/09/2026 10:14            │
│                          [Annuler]  [Enregistrer]        │
└──────────────────────────────────────────────────────────┘
```

---

## J2 — Vitrine publique & découverte
**Objectif** : un prospect trouve et consulte des annonces réellement publiées, avec recherche < 300 ms p95.

### Écrans

#### J2-E1 — Accueil public / recherche
- **US** : US-07
- **Éléments** : barre de recherche proéminente (ville, type, budget), sections « Nouveautés », réassurance, CTA « Je suis propriétaire ».

#### J2-E2 — Résultats de recherche (liste + carte)
- **US** : US-07
- **Éléments** : split view liste ↔ `MapView` (synchronisation survol carte/carte-résultat), filtres persistés en URL : **prix, ville, type, meublé/non meublé** (critères d'acceptation US-07), tri, pagination ou scroll infini, compteur de résultats, `EmptyState` avec suggestion d'élargissement des filtres.

```
┌──────────────────────────────────────────────────────────────┐
│ [🔎 Dakar]  [Type ▾][Prix ▾][Meublé ▾]      142 résultats    │
├───────────────────────────────┬──────────────────────────────┤
│ ┌───────┐ Studio meublé       │                              │
│ │ photo │ Sacré-Cœur 3        │        ◉ carte interactive   │
│ └───────┘ 120 000 XOF/mois    │      ◉        ◉              │
│ ┌───────┐ Chambre + SDB       │            ◉                 │
│ │ photo │ Liberté 6 · 65 000  │                              │
└───────────────────────────────┴──────────────────────────────┘
```

#### J2-E3 — Fiche publique du bien
- **US** : US-07, US-09, US-10
- **Éléments** : `MediaGallery` (photos zoomables + vidéo), caractéristiques (type, surface, meublé, équipements), prix et charges, carte de localisation, encart caution (activé au J4), panneau latéral d'action collant : « Demander une visite » (J3) et **« Contacter le propriétaire »**.
- **MVP** : conformément à §8.1, US-10 est **simplifiée** — formulaire de contact, coordonnées révélées après demande, pas de chat temps réel. Le même emplacement accueillera le bouton « Discuter » au V1.1.

```
┌ Studio meublé — Sacré-Cœur 3 ────────────────────────────────┐
│ ┌────────────────────────────┐ ┌──────────────────────────┐  │
│ │      galerie photo/vidéo    │ │  120 000 XOF / mois      │  │
│ │   ◄  ▣ ▣ ▣ ▣ ▣           ► │ │  Caution : 2 mois        │  │
│ └────────────────────────────┘ │  ● Disponible            │  │
│ Description · 28 m² · Meublé   │  [ Demander une visite ] │  │
│ Équipements: clim, wifi, ...   │  [ Contacter le proprio ]│  │
│ ── Localisation (carte) ────── └──────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

#### J2-E4 — Formulaire de mise en relation
- **US** : US-10 (version MVP)
- **Éléments** : modale (message, coordonnées du prospect, créneau souhaité facultatif), confirmation, puis affichage des coordonnées du propriétaire.

#### J2-E5 — Gestion des médias d'une unité (propriétaire)
- **US** : US-09
- **Éléments** : `FileUploader` multi-fichiers, réordonnancement par glisser-déposer, désignation d'**un seul média principal** (contrainte §9.6), upload vidéo avec limite de taille affichée, **blocage de publication tant que < 3 photos** (critère d'acceptation US-09) avec message explicite.

```
┌ Médias ▸ Chambre 1 ─────────────────────────────────────┐
│ ┌────┐ ┌────┐ ┌────┐   ┌──────────────┐                 │
│ │ ★  │ │    │ │    │   │  + Déposer   │                 │
│ └────┘ └────┘ └────┘   └──────────────┘                 │
│  principale  ⋮⋮ glisser pour réordonner                 │
│ ⚠ 3 photos minimum requises pour publier — OK (3/3)     │
└─────────────────────────────────────────────────────────┘
```

#### J2-E6 — Publication d'une annonce (propriétaire)
- **US** : US-09, prépare US-25
- **Éléments** : checklist de complétude (médias ✓, loyer ✓, adresse ✓, description ✓), aperçu « tel que vu par un prospect », bouton `Publier`, indicateur d'indexation (« visible dans la recherche sous 1 min », cf. critère de sortie J2).

---

## J3 — Prise de rendez-vous & mise en relation
**Objectif** : planifier une visite et échanger sans exposer ses coordonnées.

### Écrans

#### J3-E1 — Gestion des créneaux de visite (propriétaire)
- **US** : US-08
- **Éléments** : vue calendrier semaine/mois, création de créneau (unité concernée, date/heure, durée, type **individuelle / groupée / virtuelle**, capacité), créneaux récurrents, code couleur par statut de créneau.

```
┌ Visites ▸ Créneaux ────────────────────────────────────────┐
│ ◄ Sem. 38  Sept 2026 ►      [+ Créneau]  [Vue mois]        │
│      Lun 14   Mar 15   Mer 16   Jeu 17   Ven 18            │
│ 09h  ▓Ch.1▓             ▓Ch.2▓                             │
│ 11h           ▓Studio▓            ▓Ch.1▓                   │
│ 15h  ▓Ch.1 (2/3)▓                         ▓Virtuelle▓      │
└────────────────────────────────────────────────────────────┘
```

#### J3-E2 — Demande de visite (prospect)
- **US** : US-08
- **Éléments** : sélection d'un créneau disponible, nombre de participants, message, coordonnées, confirmation.
- **MVP** : US-08 est **simplifiée** (§8.1) — formulaire de demande de créneau + **confirmation manuelle du propriétaire**, sans calendrier synchronisé avancé. L'écran affiche donc « demande envoyée, en attente de confirmation », pas « visite confirmée ».

#### J3-E3 — Traitement des demandes de visite (propriétaire)
- **US** : US-08
- **Éléments** : file de demandes (`EN_ATTENTE` en tête), actions Confirmer / Refuser (motif) / Proposer un autre créneau, alerte de capacité dépassée, marquage a posteriori `HONOREE` / `ABSENT`.

#### J3-E4 — Mes visites (prospect)
- **US** : US-08
- **Éléments** : liste à venir / passées, statut (`EN_ATTENTE`, `CONFIRMEE`, `REFUSEE`, `ANNULEE`, `HONOREE`, `ABSENT`), rappel J-1, annulation, ajout au calendrier.

#### J3-E5 — Messagerie interne (chat temps réel)
- **US** : US-27
- **Éléments** : 2 colonnes — liste des conversations (interlocuteur anonymisé avant signature, dernier message, non-lus) et fil de discussion (bulles, accusés lu/livré, indicateur de saisie, pièces jointes), bandeau « vos coordonnées restent masquées jusqu'à la signature du contrat », `RealtimeBadge`.
- **Planning** : **reporté au V1.1** selon §8.1 (remplacé au MVP par J2-E4). À maquetter avec le jalon, à livrer après.

```
┌ Messages ──────────┬───────────────────────────────────────┐
│ ● Prospect #4821 2 │ Prospect #4821 · Studio Sacré-Cœur    │
│   Prospect #4790   │ ┌──────────────────────────────┐      │
│   M. Ndiaye        │ │ Le studio est-il dispo en    │      │
│                    │ │ octobre ?              10:02 │      │
│                    │ └──────────────────────────────┘      │
│                    │        ┌────────────────────────────┐ │
│                    │        │ Oui, visite possible  ✓✓  │ │
│                    │        └────────────────────────────┘ │
│                    │ 🔒 Coordonnées masquées jusqu'à signat.│
│                    │ [ Écrire un message…        ] [📎][➤] │
└────────────────────┴───────────────────────────────────────┘
```

---

## J4 — Contrat, caution, résiliation
**Objectif** : parcours contractuel de bout en bout, de la génération à la clôture.

### Écrans

#### J4-E1 — Simulateur de caution
- **US** : US-12
- **Éléments** : loyer (pré-rempli), curseur/sélecteur « nombre de mois » **plafonné à 3** avec blocage et message explicite au-delà, total calculé, échéancier d'entrée dans les lieux (caution + 1er loyer), visible aussi côté prospect sur la fiche du bien.

```
┌ Caution ───────────────────────────────────────┐
│ Loyer mensuel        120 000 XOF               │
│ Mois de caution      [1] [2] (3) [4]✕          │
│                      ⚠ Maximum légal : 3 mois  │
│ ─────────────────────────────────────────────  │
│ Caution exigée       360 000 XOF               │
│ À verser à l'entrée  480 000 XOF (caution+loyer)│
└────────────────────────────────────────────────┘
```

#### J4-E2 — Création d'un contrat (Stepper)
- **US** : US-12, US-13, US-05
- **Étapes** : 1) Unité + locataire → 2) Période, loyer, périodicité, mode de paiement préféré → 3) Caution (J4-E1 intégré) → 4) **Règles contractuelles** : gestion des dégâts, imputation, clause de pénalité de résiliation anticipée → 5) Aperçu du contrat → 6) Envoi pour signature.
- **Blocage métier** : si la période chevauche un contrat `SIGNE`/`ACTIF` sur la même unité (contrainte d'exclusion PostgreSQL), afficher une erreur bloquante lisible avec le contrat en conflit — **ce cas doit être maquetté, il sera rencontré**.

#### J4-E3 — Détail du contrat & cycle de vie
- **US** : US-11, US-12, US-13, US-22
- **Éléments** : en-tête (n° de contrat, parties, unité, période, loyer), `StatusBadge` sur le cycle `BROUILLON → EN_ATTENTE_SIGNATURE → SIGNE → ACTIF → CLOTURE` (+ `SUSPENDU`, `RESILIE`, `EXPIRE`, `ANNULE`) rendu en frise horizontale, onglets Conditions / Caution / Signatures / États des lieux / Avenants / Incidents / Documents, actions contextuelles selon le statut.

```
┌ Contrat CTR-2026-0148 ──────────────────────────────────────┐
│ ●─────●─────●─────◉─────○      Locataire : M. Ndiaye        │
│ Broui. Signat. Signé ACTIF  Clôturé                         │
│ Unité Chambre 1 · 85 000 XOF/mois · 01/10/26 → 30/09/27     │
│ [Conditions][Caution][Signatures][États des lieux][Avenants]│
│                       [Ajouter un avenant] [Résilier]       │
└─────────────────────────────────────────────────────────────┘
```

#### J4-E4 — Signature du contrat
- **US** : US-22
- **MVP** : lecture du PDF intégrée + case **« J'ai lu et j'accepte »** horodatée + double confirmation → preuve de traçabilité affichée (date, heure, IP tronquée).
- **V1.1** : remplacement par le parcours prestataire (Yousign/DocuSign) — même emplacement, bouton « Signer électroniquement » et écran de retour de signature (succès / abandon / expiration).

#### J4-E5 — État des lieux (entrée / sortie)
- **US** : US-13
- **Éléments** : parcours pièce par pièce, pour chaque élément : état (neuf/bon/usagé/dégradé), commentaire, **photos horodatées** obligatoires, puis **double validation** propriétaire + locataire (statuts « en attente de contresignature »), comparatif entrée↔sortie côte à côte à la sortie.

```
┌ État des lieux ▸ Sortie ▸ Chambre 1 ────────────────────────┐
│           ENTRÉE (01/10/26)      │      SORTIE (30/09/27)   │
│ Murs     [photo] Bon             │ [photo] Dégradé  ⚠       │
│ Sol      [photo] Bon             │ [photo] Bon              │
│ Sanitaire[photo] Neuf            │ [photo] Bon              │
│                        [+ photo horodatée]                  │
│ Validation : ☑ Propriétaire   ☐ En attente locataire        │
└─────────────────────────────────────────────────────────────┘
```

#### J4-E6 — Incidents & imputation / arbitrage des réparations
- **US** : US-13
- **Éléments** : registre d'incidents de l'unité (type, urgence, statut `SIGNALE → QUALIFIE → PRIS_EN_CHARGE → RESOLU → CLOTURE`), qualification de **responsabilité** (locataire / propriétaire / indépendant), saisie du coût de réparation avec **simulation automatique des 3 scénarios de caution** :
  - coût ≤ caution → delta remboursé au locataire ;
  - coût > caution → à la charge du locataire, aucune restitution ;
  - incident indépendant → enregistré au registre, non imputable.
- **Workflow** : proposition de montant → contre-proposition de l'autre partie → accord ou escalade admin (module litiges, J8).
- **Signalement** : le backlog prévoit « arbitrage admin en cas de désaccord » mais **ne définit ni délai de réponse, ni nombre de contre-propositions autorisées** — nécessaire pour finaliser cet écran.

```
┌ Incident INC-0091 · Mur dégradé ────────────────────────────┐
│ Urgence [Normale ▾]  Responsabilité (•)Locataire ( )Proprio │
│                                      ( )Indépendant         │
│ Coût de réparation  [ 150 000 XOF ]                         │
│ ── Impact sur la caution ─────────────────────────────────  │
│ Caution 360 000 − Réparation 150 000 = 210 000 restitués    │
│ Statut : ⏳ en attente d'accord du locataire                │
└─────────────────────────────────────────────────────────────┘
```

#### J4-E7 — Résiliation anticipée
- **US** : US-11
- **Éléments** : rappel de la clause de pénalité (affichée **avant** signature), simulateur (date de sortie souhaitée → pénalité calculée + solde de tout compte prévisionnel incluant la caution), motif, confirmation, **génération d'un avenant de résiliation** téléchargeable.

#### J4-E8 — Espace documentaire
- **US** : US-23
- **Éléments** : arborescence par catégorie (Contrats, Quittances, CNI, États des lieux, Avenants), recherche, filtre par contrat/bien, prévisualisation, téléchargement via **URL signée à durée courte**, mention « accès tracé », versions.

```
┌ Mes documents ───────────────────────────────────────────────┐
│ [🔎] [Catégorie ▾][Contrat ▾]                                │
│ 📁 Contrats (3)                                              │
│    📄 CTR-2026-0148.pdf        signé 28/09/26   [👁][⤓]      │
│ 📁 Quittances (11)                                           │
│ 📁 États des lieux (2)   📁 CNI (2) 🔒                       │
└──────────────────────────────────────────────────────────────┘
```

---

## J5 — Paiement, quittances, suivi financier
**Objectif** : cycle d'encaissement opérationnel, dashboard à jour en < 2 s.

### Écrans

#### J5-E1 — Dashboard financier propriétaire
- **US** : US-04
- **Éléments** : `StatCard` (taux d'occupation, revenus encaissés du mois, impayés, échéances à venir), graphique de revenus **mensuel/hebdomadaire** commutable, liste d'alertes visuelles pour les impayés > X jours (seuil paramétrable), répartition par bien, `RealtimeBadge`.

```
┌ Tableau de bord ───────────────────────────  ⚡ temps réel ─┐
│ ┌─────────┐┌─────────┐┌─────────┐┌─────────┐               │
│ │Occupation││Encaissé ││ Impayés ││À venir  │               │
│ │  87 %   ││1 240 000││ 170 000 ││ 340 000 │               │
│ └─────────┘└─────────┘└─────────┘└─────────┘               │
│ Revenus  [Mensuel|Hebdo]     ▁▃▅▆█▇▅                        │
│ ⚠ Alertes                                                   │
│  • M. Ndiaye — Ch.1 — 45 j de retard — 85 000 XOF  [Relancer]│
└─────────────────────────────────────────────────────────────┘
```

#### J5-E2 — Suivi des loyers (payés / impayés)
- **US** : US-03
- **Éléments** : `DataTable` des échéances (bien, unité, locataire, période, montant dû, montant reçu, reste, échéance, statut), **code couleur payé / en retard / partiel** (critère d'acceptation US-03), filtres par bien / locataire / période / statut, groupement par bien ou par locataire, export.

```
┌ Loyers ──────────────────────────────────────────────────────┐
│ [Bien ▾][Statut ▾][Période: Sept 2026 ▾]        [Export CSV] │
├────────────┬────────┬──────────┬──────────┬─────────────────┤
│ Locataire  │ Unité  │ Dû       │ Reçu     │ Statut          │
│ M. Ndiaye  │ Ch.1   │  85 000  │      0   │ 🔴 En retard 45j│
│ A. Sow     │ Studio │ 120 000  │ 60 000   │ 🟠 Partiel      │
│ F. Ba      │ Ch.2   │  65 000  │ 65 000   │ 🟢 Payé         │
└────────────┴────────┴──────────┴──────────┴─────────────────┘
```

#### J5-E3 — Historique par locataire
- **US** : US-03
- **Éléments** : frise des échéances du contrat, cumul payé/dû, taux de ponctualité, accès aux quittances.

#### J5-E4 — Mes loyers (locataire)
- **US** : US-03, US-18
- **Éléments** : carte « prochaine échéance » avec compte à rebours et bouton `Payer`, historique, alerte de retard, accès aux quittances.

#### J5-E5 — Parcours de paiement
- **US** : US-18
- **Éléments** : récapitulatif de l'échéance, choix du moyen (**Orange Money, Wave, Free Money, carte bancaire**), saisie du numéro Mobile Money, écran d'attente de confirmation du fournisseur (état `PENDING`, polling/WebSocket), écrans de retour **succès / échec / expiré** avec action de reprise.
- **Points critiques à maquetter** : idempotence — le double-clic ou le retour arrière ne doit jamais créer un second paiement (bouton verrouillé + message « paiement déjà en cours ») ; indisponibilité du fournisseur (circuit breaker ouvert) → message « paiement mis en file d'attente, vous serez notifié », pas une erreur brute.

```
┌ Paiement ─ Échéance oct. 2026 ─ 85 000 XOF ─────────────┐
│ Moyen de paiement                                        │
│  (•) Orange Money   ( ) Wave   ( ) Free Money  ( ) Carte │
│  Numéro  [+221 __ ___ __ __]                             │
│  [        Payer 85 000 XOF        ]                      │
├──────────────────────────────────────────────────────────┤
│  ⏳ Confirmez sur votre téléphone… (expire dans 04:32)   │
│     Ne fermez pas cette page                             │
└──────────────────────────────────────────────────────────┘
```

#### J5-E6 — Notification temps réel & centre de notifications
- **US** : US-19
- **Éléments** : toast « Paiement reçu — 85 000 XOF — M. Ndiaye » avec lien vers la quittance, panneau latéral de notifications (non-lues, filtres par type), mise à jour instantanée des `StatCard` du dashboard.

#### J5-E7 — Quittances
- **US** : US-21
- **Éléments** : liste des quittances (n°, période, locataire, montant, date d'émission), aperçu PDF, téléchargement, envoi au locataire, génération automatique déclenchée **uniquement quand l'échéance est soldée** (règle §9.8) — un état « quittance en attente de solde » doit être visible pour les paiements partiels.

---

## J6 — Relances automatiques & notifications multi-canal
**Objectif** : réduire les impayés sans intervention manuelle.

### Écrans

#### J6-E1 — Règles de relance
- **US** : US-20
- **Éléments** : configuration des déclencheurs (J-X avant échéance, J+X après), sélection des canaux par palier (email / SMS / WhatsApp), modèles de message avec variables (`{locataire}`, `{montant}`, `{échéance}`) et aperçu rendu, plages horaires d'envoi, activation par bien ou globale, **test d'envoi**.

```
┌ Relances automatiques ────────────────────  [● Activé]──────┐
│ Palier 1  J-3 avant échéance   [x]Email [ ]SMS [ ]WhatsApp  │
│ Palier 2  J+1 après échéance   [x]Email [x]SMS              │
│ Palier 3  J+7 après échéance   [x]Email [x]SMS [x]WhatsApp  │
│ Modèle « Palier 2 » :                                       │
│  Bonjour {locataire}, votre loyer de {montant} du           │
│  {échéance} reste impayé…                   [Aperçu][Test]  │
│ Fenêtre d'envoi  [08:00] → [20:00]                          │
└─────────────────────────────────────────────────────────────┘
```

#### J6-E2 — Journal des relances & envois
- **US** : US-20
- **Éléments** : table des envois (destinataire, canal, palier, date, statut `envoyé / délivré / échec`), motif d'échec, **relance manuelle**, indicateur de non-duplication, filtres.
- **Écran technique associé** : voir J9-E1 pour la file d'erreurs (DLQ).

#### J6-E3 — Préférences de notification (utilisateur)
- **US** : US-20, US-06
- **Éléments** : matrice type d'événement × canal, avec les canaux issus d'US-06 (email / SMS / WhatsApp / Telegram / Signal), désabonnement des envois non essentiels.
- **Signalement** : US-06 cite Telegram et Signal comme canaux de relance, mais la section 4 ne retient que Twilio (SMS/WhatsApp), SendGrid/Mailgun et FCM. **Telegram et Signal n'ont pas de fournisseur défini** — ces canaux sont affichés désactivés avec la mention « bientôt disponible » tant que l'arbitrage n'est pas fait.

---

## J7 — Réservation temps réel & anti double-booking
**Objectif** : cohérence garantie de la disponibilité.

### Écrans

#### J7-E1 — Réservation d'un bien avec verrou temporaire
- **US** : US-24
- **Éléments** : bouton « Réserver » sur la fiche publique, modale de confirmation expliquant le verrou (durée, ex. 24 h), **compte à rebours persistant** sur la réservation, actions « poursuivre vers le contrat » / « libérer », écran d'échec temps réel si un autre prospect a obtenu le verrou en premier (« ce bien vient d'être réservé », **sans rechargement de page**).

```
┌ Réservation — Studio Sacré-Cœur ────────────────────────────┐
│ 🔒 Ce bien vous est réservé                                  │
│    Temps restant  ⏱ 23:41:12                                │
│    Passé ce délai, il redevient disponible.                  │
│    [ Poursuivre vers le contrat ]   [ Libérer la réservation]│
└──────────────────────────────────────────────────────────────┘
                    ── ou, en cas de concurrence perdue ──
┌──────────────────────────────────────────────────────────────┐
│ ⚠ Ce bien vient d'être réservé par un autre candidat.        │
│   [ Voir des biens similaires ]  [ M'alerter s'il se libère ]│
└──────────────────────────────────────────────────────────────┘
```
- **Signalement** : « m'alerter s'il se libère » est une proposition d'UX, **non couverte par une US** — à valider ou retirer.

#### J7-E2 — Mes réservations (prospect)
- **US** : US-24
- **Éléments** : liste des verrous actifs avec comptes à rebours, historique des réservations expirées/converties.

#### J7-E3 — Publication & disponibilité temps réel (propriétaire)
- **US** : US-25
- **Éléments** : bascule Publier/Dépublier par unité avec confirmation, statut en direct (`Disponible / Réservé / Occupé / Dépublié / En travaux`), file des réservations en cours avec comptes à rebours, impact sur la recherche affiché (« retiré de la recherche en < 1 min »).

---

## J8 — Confiance, litiges, qualité de service *(Should Have)*
**Objectif** : renforcer la confiance sans risque sur le cœur applicatif — fonctionnalités livrables sous feature flag.

### Écrans

#### J8-E1 — Dépôt d'un avis
- **US** : US-26
- **Éléments** : note en étoiles (par critère : conformité de l'annonce, réactivité, propreté), commentaire libre, déclenchement **après résiliation/clôture du contrat**, avis croisé propriétaire↔locataire.
- **Signalement** : le backlog ne précise ni la **modération a priori/a posteriori**, ni le droit de réponse, ni le délai de dépôt. À trancher avant conception finale.

#### J8-E2 — Affichage des avis (fiche publique & profil)
- **US** : US-26
- **Éléments** : note moyenne, distribution par étoiles, liste paginée, badge « locataire vérifié ».

#### J8-E3 — Ouverture et suivi d'un litige
- **US** : US-28
- **Éléments** : formulaire (objet : réparation contestée / caution non restituée, contrat lié, montant contesté, pièces justificatives), fil de discussion tripartite, frise de statut.

#### J8-E4 — Back-office d'arbitrage (admin)
- **US** : US-28
- **Éléments** : file de litiges priorisée (ancienneté, montant), vue dossier complet (contrat, état des lieux entrée/sortie côte à côte, incidents, paiements, échanges), formulaire de décision avec motivation obligatoire, **effet de la décision sur la caution**, journal d'audit intégral.

```
┌ Admin ▸ Litige LIT-0032 ────────────────────────────────────┐
│ Caution non restituée · 210 000 XOF · ouvert il y a 6 j     │
│ ┌ Preuves ───────────────┐ ┌ Décision ───────────────────┐  │
│ │ EDL entrée │ EDL sortie│ │ ( ) En faveur du locataire  │  │
│ │  [photo]   │  [photo]  │ │ ( ) En faveur du propriétaire│ │
│ │ Incident INC-0091      │ │ ( ) Répartition : [___] XOF │  │
│ │ Paiements (11)         │ │ Motivation [_______________]│  │
│ └────────────────────────┘ │ [ Rendre la décision ]      │  │
│                            └─────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

#### J8-E5 — Sélecteur de langue & préférences régionales
- **US** : US-30
- **Éléments** : sélecteur dans le header et dans les paramètres, persistance par utilisateur, formats de date/monnaie adaptés.
- **Signalement** : la seconde langue n'est **pas tranchée** (« anglais/wolof selon le marché cible »). Impact réel sur la conception : le wolof implique de revalider la longueur des libellés et l'absence de terminologie standard pour des termes comme « caution » ou « quittance ». Décision requise avant de figer les composants.

---

## J9 — Durcissement production
**Objectif** : valider la tenue en charge et la supervision avant ouverture large.

### Écrans

#### J9-E1 — Console de supervision (admin technique)
- **US** : US-29
- **Éléments** : bandeau de santé des services (API, PostgreSQL, Redis, Kafka/Redpanda, Meilisearch, MinIO, Keycloak) alimenté par Actuator, **paiements en échec** avec possibilité de rejeu, **jobs de notification en erreur** et file DLQ avec rejeu unitaire/en masse, état des circuit breakers Resilience4j par fournisseur externe, latences p95 par endpoint, liens profonds vers Grafana/Jaeger plutôt que réimplémentation des graphes.

```
┌ Supervision ────────────────────────────────────────────────┐
│ API ●  PostgreSQL ●  Redis ●  Kafka ●  Meili ●  MinIO ◐     │
│ Circuit breakers : OrangeMoney ●fermé · Wave ◉OUVERT · SMS ● │
├──────────────────────┬──────────────────────────────────────┤
│ Paiements en échec 7 │ DLQ notifications           23       │
│  PAY-4821 timeout ⟳  │  relance SMS · 12 msg   [Rejouer]    │
│  PAY-4822 refusé  ⟳  │  quittance PDF · 11 msg [Rejouer]    │
├──────────────────────┴──────────────────────────────────────┤
│ Latence p95 : /api/v1/biens 180ms · /paiements 410ms         │
│ [Ouvrir Grafana] [Ouvrir Jaeger]                             │
└──────────────────────────────────────────────────────────────┘
```

#### J9-E2 — Journal d'audit des accès sensibles
- **US** : US-29, US-17
- **Éléments** : recherche par utilisateur / ressource / période, type d'accès (consultation CNI, téléchargement de document, décision de litige), export pour conformité.

---

## Récapitulatif — priorisation des écrans par jalon

| Jalon | Écrans | Statut MVP |
|---|---|---|
| J0 | E1 Inscription · E2 Connexion/OTP · E3 Reset MDP · E4 Sécurité compte · E5 Consentements · E6 Admin rôles · E7 États système | Intégral |
| J1 | E1 Accueil · E2 Liste biens · E3 Création bien · E4 Détail bien · E5 Périodicité · E6 Liste locataires · E7 Dossier locataire | Intégral |
| J2 | E1 Accueil public · E2 Recherche · E3 Fiche bien · E4 Mise en relation · E5 Médias · E6 Publication | Intégral sauf E4 (US-10 simplifiée) |
| J3 | E1 Créneaux · E2 Demande visite · E3 Traitement demandes · E4 Mes visites · E5 Messagerie | E1-E4 simplifiés ; **E5 reporté V1.1** |
| J4 | E1 Simulateur caution · E2 Création contrat · E3 Détail contrat · E4 Signature · E5 État des lieux · E6 Incidents · E7 Résiliation · E8 Documents | Intégral ; **E4 simplifié au MVP** |
| J5 | E1 Dashboard · E2 Suivi loyers · E3 Historique · E4 Mes loyers · E5 Paiement · E6 Notifications · E7 Quittances | Intégral |
| J6 | E1 Règles relance · E2 Journal envois · E3 Préférences | **Hors MVP — V1.1** |
| J7 | E1 Réservation/verrou · E2 Mes réservations · E3 Publication temps réel | **Hors MVP — V1.2** |
| J8 | E1 Dépôt avis · E2 Affichage avis · E3 Litige · E4 Arbitrage admin · E5 Langue | **V1.2 / V2** |
| J9 | E1 Supervision · E2 Audit | **V2 / continu** |

---

## Informations manquantes au backlog (à clarifier avant conception détaillée)

1. **Charte de marque** — aucun nom de produit, logo, palette ou typographie n'est défini. Les tokens de la section 0 sont des placeholders structurels.
2. **Périmètre mobile** — React Native est retenu (§4.1) mais **aucune US ne décrit les parcours mobiles** ni ne dit s'ils sont iso-web. Les écrans ci-dessus sont pensés responsive web ; des maquettes natives nécessitent un cadrage dédié.
3. **Structure du dossier locataire** — module `locataires` « à concevoir au J1 », hors draft SQL. Seuls email, téléphone et CNI sont spécifiés (US-06) : garant, justificatifs de revenus, situation professionnelle ne sont ni exigés ni exclus.
4. **Rôle « gestionnaire délégué »** — cité comme « éventuel » en US-16, sans périmètre de permissions.
5. **Workflow d'arbitrage des réparations (US-13)** — délais de réponse et nombre de contre-propositions non définis.
6. **Canaux Telegram / Signal (US-06)** — sans fournisseur retenu en section 4.
7. **Seconde langue (US-30)** — anglais ou wolof non tranché ; impact direct sur les libellés et la terminologie métier.
8. **Modération des avis (US-26)** — a priori ou a posteriori, droit de réponse, délai de dépôt : non spécifiés.
9. **Export comptable** — cité en « Should Have » (§5) sans US dédiée : aucun écran conçu, périmètre inconnu (format, référentiel comptable).
10. **Délais RGPD** — politique de conservation/suppression exigée (§11.5) mais sans délai chiffré à afficher à l'utilisateur.
11. **Périodicité par bien ou par unité (US-05)** — le backlog dit « par bien », le modèle de données rend l'unité louable ; incohérence à trancher.
12. **Écran de connexion : Keycloak themé ou écran applicatif** — à formaliser dans l'ADR-004.
