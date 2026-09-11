CREATE SCHEMA IF NOT EXISTS avis;

CREATE TABLE avis.avis (
    id UUID PRIMARY KEY,
    auteur_id UUID NOT NULL,
    cible_unite_id UUID,
    cible_utilisateur_id UUID,
    note SMALLINT NOT NULL,
    commentaire TEXT,
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_avis_note CHECK (note BETWEEN 1 AND 5),
    CONSTRAINT chk_avis_cible CHECK (cible_unite_id IS NOT NULL OR cible_utilisateur_id IS NOT NULL)
);

CREATE UNIQUE INDEX idx_avis_auteur_unite ON avis.avis (auteur_id, cible_unite_id) WHERE cible_unite_id IS NOT NULL;
CREATE UNIQUE INDEX idx_avis_auteur_user ON avis.avis (auteur_id, cible_utilisateur_id) WHERE cible_utilisateur_id IS NOT NULL;
