CREATE SCHEMA IF NOT EXISTS biens;

CREATE TABLE biens.bien_immobilier (
    id UUID PRIMARY KEY,
    proprietaire_id UUID NOT NULL,
    designation VARCHAR(200) NOT NULL,
    type VARCHAR(32) NOT NULL,
    adresse VARCHAR(255),
    ville VARCHAR(120),
    latitude NUMERIC(10, 7),
    longitude NUMERIC(10, 7),
    statut VARCHAR(32) NOT NULL DEFAULT 'BROUILLON',
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    maj_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_bien_proprietaire ON biens.bien_immobilier (proprietaire_id);

CREATE TABLE biens.unite_locative (
    id UUID PRIMARY KEY,
    bien_id UUID NOT NULL REFERENCES biens.bien_immobilier (id),
    libelle VARCHAR(120) NOT NULL,
    type VARCHAR(32) NOT NULL,
    surface_m2 NUMERIC(10, 2),
    meuble BOOLEAN NOT NULL DEFAULT FALSE,
    loyer NUMERIC(19, 2) NOT NULL,
    devise VARCHAR(3) NOT NULL DEFAULT 'XOF',
    periodicite VARCHAR(16) NOT NULL DEFAULT 'MENSUEL',
    jour_echeance SMALLINT,
    statut VARCHAR(32) NOT NULL DEFAULT 'LIBRE',
    publie BOOLEAN NOT NULL DEFAULT FALSE,
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    maj_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_unite_bien ON biens.unite_locative (bien_id);
