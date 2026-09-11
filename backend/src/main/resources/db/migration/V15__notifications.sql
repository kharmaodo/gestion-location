CREATE SCHEMA IF NOT EXISTS notifications;

CREATE TABLE notifications.notification (
    id UUID PRIMARY KEY,
    destinataire_id UUID NOT NULL,
    type VARCHAR(64) NOT NULL,
    message TEXT NOT NULL,
    lu BOOLEAN NOT NULL DEFAULT FALSE,
    cree_le TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notif_dest ON notifications.notification (destinataire_id, cree_le DESC);
