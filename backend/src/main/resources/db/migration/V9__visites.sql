CREATE SCHEMA IF NOT EXISTS visites;

CREATE TABLE visites.visite (
    id UUID PRIMARY KEY,
    unite_id UUID NOT NULL,
    proprietaire_id UUID NOT NULL,
    nom VARCHAR(120) NOT NULL,
    telephone VARCHAR(32),
    email VARCHAR(180),
    creneau TIMESTAMPTZ NOT NULL,
    statut VARCHAR(32) NOT NULL DEFAULT 'DEMANDEE',
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_visite_proprio ON visites.visite (proprietaire_id);
CREATE INDEX idx_visite_unite ON visites.visite (unite_id);
