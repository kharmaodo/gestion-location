package com.location.admin.dto;

public record OpsSnapshotResponse(
        long intentionsEnAttente,
        long intentionsEchec,
        long intentionsReussies,
        long signaturesEnAttente,
        long signaturesSignees,
        long litigesOuverts) {}
