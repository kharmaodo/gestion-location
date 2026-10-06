# Release v1.0.0

Apres merge :

```bash
git checkout develop && git pull
git tag v1.0.0
git push origin v1.0.0
```

Images :

- ghcr.io/kharmaodo/gestion-location-backend:v1.0.0
- ghcr.io/kharmaodo/gestion-location-web:v1.0.0

ArgoCD prod suit `targetRevision: v1.0.0`.
