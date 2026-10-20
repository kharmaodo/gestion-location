# Ameliorations user-friendly

Liste des ameliorations d'interface qui ne changent pas le contrat API.

## En cours

### Select destinataire (messages)
- Avant : saisie libre d'un UUID destinataire.
- Apres : liste deroulante prenom + nom. La valeur envoyee reste l'UUID.
- Endpoint ajoute : `GET /api/v1/conversations/destinataires` -> `{ id, prenom, nom }[]`.
- Contrat inchange : `POST /api/v1/conversations` avec `{ destinataireId }`.
- La liste des conversations affiche les noms quand ils sont connus.

## A faire

- Recherche dans le select destinataire si la liste grossit.
- Filtrer les destinataires par role (proprietaire voit locataires, et inversement).
- Afficher le nom dans le detail d'une conversation, pas seulement dans la liste.
