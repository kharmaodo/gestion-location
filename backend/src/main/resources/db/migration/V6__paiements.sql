CREATE SCHEMA IF NOT EXISTS paiements;

CREATE TABLE paiements.echeance (
    id UUID PRIMARY KEY,
    contrat_id UUID NOT NULL,
    proprietaire_id UUID NOT NULL,
    periode_debut DATE NOT NULL,
    periode_fin DATE NOT NULL,
    montant NUMERIC(19, 2) NOT NULL,
    devise VARCHAR(3) NOT NULL DEFAULT 'XOF',
    statut VARCHAR(32) NOT NULL DEFAULT 'A_PAYER',
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    maj_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_echeance_contrat ON paiements.echeance (contrat_id);
CREATE INDEX idx_echeance_proprio ON paiements.echeance (proprietaire_id);

CREATE TABLE paiements.paiement (
    id UUID PRIMARY KEY,
    echeance_id UUID NOT NULL REFERENCES paiements.echeance (id),
    montant NUMERIC(19, 2) NOT NULL,
    mode VARCHAR(32) NOT NULL DEFAULT 'ESPECES',
    reference VARCHAR(80),
    recu_numero VARCHAR(40) NOT NULL,
    paye_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
