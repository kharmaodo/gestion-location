CREATE TABLE contrats.signature (
    id UUID PRIMARY KEY,
    contrat_id UUID NOT NULL,
    proprietaire_id UUID NOT NULL,
    role_signataire VARCHAR(32) NOT NULL,
    nom_signataire VARCHAR(160) NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    signe_le TIMESTAMPTZ,
    statut VARCHAR(32) NOT NULL DEFAULT 'EN_ATTENTE',
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sig_contrat ON contrats.signature (contrat_id);
