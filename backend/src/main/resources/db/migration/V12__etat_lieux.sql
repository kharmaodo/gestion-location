CREATE SCHEMA IF NOT EXISTS contrats;

CREATE TABLE IF NOT EXISTS contrats.etat_lieux (
    id UUID PRIMARY KEY,
    contrat_id UUID NOT NULL,
    proprietaire_id UUID NOT NULL,
    type VARCHAR(16) NOT NULL,
    observations TEXT,
    cout_reparations NUMERIC(19, 2) NOT NULL DEFAULT 0,
    statut VARCHAR(32) NOT NULL DEFAULT 'BROUILLON',
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_edl_contrat_type ON contrats.etat_lieux (contrat_id, type);
