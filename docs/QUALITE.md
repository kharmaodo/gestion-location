# Qualite

## Unitaire
`mvn -B test` dans `backend`. Le workflow `quality` le lance sur chaque PR.

## Sonar
Secrets `SONAR_TOKEN` et `SONAR_HOST_URL`. Sans token, le job Sonar est ignore. Scanner : `sonar-project.properties`.

## Charge et rate-limit
Backend local, puis :

```bash
k6 run load/k6-sante.js
# alternative
ab -n 200 -c 20 http://localhost:8080/actuator/health
```

Le scenario k6 accepte 200 sur `/actuator/health` et 401 ou 429 sur un login inconnu. Le 429 n'apparaitra que si un rate-limit est branche devant l'API (Spring Cloud Gateway, NGINX ou Envoy).

## Intrusion
Baseline open source, API locale :

```bash
docker run --rm -t ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t http://host.docker.internal:8080/actuator/health
```

## Repartition de charge
Le chart Helm prod a `replicaCount: 2`. Le load-balancing est celui du Service Kubernetes. Il n'y a pas encore d'Ingress controller ni de Gateway dans le chart.
