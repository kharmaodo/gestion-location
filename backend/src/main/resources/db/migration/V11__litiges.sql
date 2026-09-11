CREATE SCHEMA IF NOT EXISTS litiges;

CREATE TABLE litiges.litige (
    id UUID PRIMARY KEY,
    contrat_id UUID NOT NULL,
    proprietaire_id UUID NOT NULL,
    auteur_id UUID NOT NULL,
    motif VARCHAR(64) NOT NULL,
    description TEXT NOT NULL,
    statut VARCHAR(32) NOT NULL DEFAULT 'OUVERT',
    decision TEXT,
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    maj_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_litige_contrat ON litiges.litige (contrat_id);
CREATE INDEX idx_litige_proprio ON litiges.litige (proprietaire_id);
