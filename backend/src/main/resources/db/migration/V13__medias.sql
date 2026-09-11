CREATE TABLE biens.media (
    id UUID PRIMARY KEY,
    unite_id UUID NOT NULL,
    url TEXT NOT NULL,
    type VARCHAR(16) NOT NULL DEFAULT 'PHOTO',
    position INT NOT NULL DEFAULT 0,
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_media_unite ON biens.media (unite_id, position);
