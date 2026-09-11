CREATE TABLE paiements.intention (
    id UUID PRIMARY KEY,
    echeance_id UUID NOT NULL REFERENCES paiements.echeance (id),
    proprietaire_id UUID NOT NULL,
    fournisseur VARCHAR(32) NOT NULL,
    montant NUMERIC(19, 2) NOT NULL,
    statut VARCHAR(32) NOT NULL DEFAULT 'EN_ATTENTE',
    reference_externe VARCHAR(80),
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
