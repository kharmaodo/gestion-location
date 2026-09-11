CREATE SCHEMA IF NOT EXISTS messagerie;

CREATE TABLE messagerie.conversation (
    id UUID PRIMARY KEY,
    participant_a UUID NOT NULL,
    participant_b UUID NOT NULL,
    unite_id UUID,
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    maj_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_conv_participants ON messagerie.conversation (
    LEAST(participant_a, participant_b),
    GREATEST(participant_a, participant_b),
    COALESCE(unite_id, '00000000-0000-0000-0000-000000000000')
);

CREATE TABLE messagerie.message (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES messagerie.conversation (id),
    auteur_id UUID NOT NULL,
    corps TEXT NOT NULL,
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_msg_conv ON messagerie.message (conversation_id, cree_le);
