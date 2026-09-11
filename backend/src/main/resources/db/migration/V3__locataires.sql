CREATE SCHEMA IF NOT EXISTS locataires;

CREATE TABLE locataires.dossier (
    id UUID PRIMARY KEY,
    proprietaire_id UUID NOT NULL,
    utilisateur_id UUID,
    prenom VARCHAR(80),
    nom VARCHAR(80) NOT NULL,
    telephone VARCHAR(32),
    email VARCHAR(180),
    piece_type VARCHAR(32),
    piece_numero VARCHAR(64),
    kyc_statut VARCHAR(32) NOT NULL DEFAULT 'EN_ATTENTE',
    kyc_commentaire TEXT,
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    maj_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_dossier_proprietaire ON locataires.dossier (proprietaire_id);

CREATE TABLE locataires.document (
    id UUID PRIMARY KEY,
    dossier_id UUID NOT NULL REFERENCES locataires.dossier (id),
    type VARCHAR(32) NOT NULL,
    nom_fichier VARCHAR(255) NOT NULL,
    chemin VARCHAR(500) NOT NULL,
    mime VARCHAR(120),
    kyc_statut VARCHAR(32) NOT NULL DEFAULT 'EN_ATTENTE',
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_document_dossier ON locataires.document (dossier_id);
