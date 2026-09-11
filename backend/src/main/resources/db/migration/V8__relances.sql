CREATE TABLE paiements.relance (
    id UUID PRIMARY KEY,
    echeance_id UUID NOT NULL REFERENCES paiements.echeance (id),
    canal VARCHAR(32) NOT NULL DEFAULT 'EMAIL',
    message TEXT NOT NULL,
    envoyee_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_relance_echeance ON paiements.relance (echeance_id);
