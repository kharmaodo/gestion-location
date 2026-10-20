# Ameliorations user-friendly

Liste des ameliorations d'interface qui ne changent pas le contrat API.

## En cours

### Select destinataire (messages)
- Avant : saisie libre d'un UUID destinataire.
- Apres : liste deroulante prenom + nom (+ email). La valeur envoyee reste l'UUID.
- Endpoint : `GET /api/v1/conversations/destinataires` -> `{ id, prenom, nom, email }[]`.
- Filtre : role oppose (proprietaire voit locataires, locataire voit proprietaires), statut ACTIF, nom non vide, dedoublonnage par email (les comptes smoke du meme email n'apparaissent qu'une fois).
- Contrat inchange : `POST /api/v1/conversations` avec `{ destinataireId }`.
- La liste des conversations affiche les noms quand ils sont connus.

## A faire

- Recherche dans le select destinataire si la liste grossit.
- Limiter aux contreparties liees par un contrat ou un dossier.
- Afficher le nom dans le detail d'une conversation, pas seulement dans la liste.
