# Smoke tests API (curl)

Prérequis : Postgres/Redis up, backend `mvn spring-boot:run`.

```bash
chmod +x scripts/api-smoke.sh
API=http://localhost:8080 ./scripts/api-smoke.sh
```

Couvre : health, register/login proprio+locataire, biens/unités/publication,
vitrine, réservation, dossier KYC, contrat+activation, échéances, paiement partiel,
relances, dashboard, messagerie, isolation rôle 403.
