CREATE SCHEMA IF NOT EXISTS reservations;

CREATE TABLE reservations.reservation (
    id UUID PRIMARY KEY,
    unite_id UUID NOT NULL,
    proprietaire_id UUID NOT NULL,
    locataire_user_id UUID,
    nom VARCHAR(80) NOT NULL,
    prenom VARCHAR(80),
    telephone VARCHAR(32),
    email VARCHAR(180),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    message TEXT,
    statut VARCHAR(32) NOT NULL DEFAULT 'EN_ATTENTE',
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    maj_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_resa_unite ON reservations.reservation (unite_id);
CREATE INDEX idx_resa_proprio ON reservations.reservation (proprietaire_id);
