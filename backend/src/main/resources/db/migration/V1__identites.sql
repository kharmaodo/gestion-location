CREATE SCHEMA IF NOT EXISTS identites;

CREATE TABLE identites.utilisateur (
    id UUID PRIMARY KEY,
    email VARCHAR(255),
    telephone VARCHAR(32),
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    prenom VARCHAR(120),
    nom VARCHAR(120),
    statut VARCHAR(32) NOT NULL DEFAULT 'ACTIF',
    email_verifie BOOLEAN NOT NULL DEFAULT FALSE,
    telephone_verifie BOOLEAN NOT NULL DEFAULT FALSE,
    two_factor_active BOOLEAN NOT NULL DEFAULT FALSE,
    two_factor_secret VARCHAR(64),
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    maj_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_utilisateur_email UNIQUE (email),
    CONSTRAINT uq_utilisateur_telephone UNIQUE (telephone),
    CONSTRAINT ck_utilisateur_identifiant CHECK (email IS NOT NULL OR telephone IS NOT NULL)
);

CREATE TABLE identites.utilisateur_role (
    id UUID PRIMARY KEY,
    utilisateur_id UUID NOT NULL REFERENCES identites.utilisateur (id),
    role VARCHAR(32) NOT NULL,
    CONSTRAINT uq_utilisateur_role UNIQUE (utilisateur_id, role)
);

CREATE TABLE identites.consentement (
    id UUID PRIMARY KEY,
    utilisateur_id UUID NOT NULL REFERENCES identites.utilisateur (id),
    type VARCHAR(64) NOT NULL,
    source VARCHAR(64) NOT NULL,
    accepte BOOLEAN NOT NULL,
    horodatage TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE identites.refresh_token (
    id UUID PRIMARY KEY,
    utilisateur_id UUID NOT NULL REFERENCES identites.utilisateur (id),
    jti_hash VARCHAR(128) NOT NULL UNIQUE,
    expire_le TIMESTAMPTZ NOT NULL,
    revoque BOOLEAN NOT NULL DEFAULT FALSE,
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE identites.reset_password (
    id UUID PRIMARY KEY,
    utilisateur_id UUID NOT NULL REFERENCES identites.utilisateur (id),
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    expire_le TIMESTAMPTZ NOT NULL,
    utilise BOOLEAN NOT NULL DEFAULT FALSE
);
