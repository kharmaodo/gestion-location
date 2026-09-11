CREATE SCHEMA IF NOT EXISTS contrats;

CREATE TABLE contrats.contrat (
    id UUID PRIMARY KEY,
    proprietaire_id UUID NOT NULL,
    unite_id UUID NOT NULL,
    dossier_id UUID,
    reservation_id UUID,
    date_debut DATE NOT NULL,
    date_fin DATE,
    loyer NUMERIC(19, 2) NOT NULL,
    devise VARCHAR(3) NOT NULL DEFAULT 'XOF',
    periodicite VARCHAR(16) NOT NULL,
    jour_echeance SMALLINT,
    caution NUMERIC(19, 2),
    statut VARCHAR(32) NOT NULL DEFAULT 'BROUILLON',
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    maj_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_contrat_proprio ON contrats.contrat (proprietaire_id);
CREATE INDEX idx_contrat_unite ON contrats.contrat (unite_id);

CREATE TABLE contrats.avenant (
    id UUID PRIMARY KEY,
    contrat_id UUID NOT NULL REFERENCES contrats.contrat (id),
    motif TEXT NOT NULL,
    periodicite VARCHAR(16),
    loyer NUMERIC(19, 2),
    date_effet DATE NOT NULL,
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
