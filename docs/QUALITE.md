# Qualite

## Unitaire
- Backend : `cd backend && mvn -B test`
- Frontend : `cd frontend-web && npm test`
- Le workflow `quality` lance les deux sur chaque PR.

## Charge et rate-limit
Backend demarre, depuis la racine :

```bash
k6 run load/k6-sante.js
ab -n 200 -c 20 http://localhost:8080/actuator/health
```

`POST /api/v1/auth/login` est limite a 30/min/IP (`LOGIN_RATE_PER_MINUTE`). Le health n'est pas limite.

## Intrusion
Backend local, puis :

```bash
sh scripts/zap-baseline.sh
```

## Sonar
Secrets `SONAR_TOKEN` et `SONAR_HOST_URL`, puis lancement manuel du workflow `quality`. JaCoCo ecrit `backend/target/site/jacoco/jacoco.xml`.

## Repartition de charge
Le chart prod a `replicaCount: 2`. La repartition est celle du Service Kubernetes. Pas d'Ingress dans le chart.
