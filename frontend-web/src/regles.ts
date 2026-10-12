export const PLAFOND_MOIS = 3;
export const PREAVIS_MIN = 30;

export function equivalentMensuel(loyer: number, periodicite?: string) {
  const per = (periodicite ?? "MENSUEL").toUpperCase();
  if (per === "JOURNALIER") return loyer * 30;
  if (per === "HEBDOMADAIRE") return loyer * 4;
  return loyer;
}

export function simulerCaution(loyer: number, periodicite: string, mois: number) {
  const demandes = mois < 1 ? 1 : mois;
  const retenus = Math.min(demandes, PLAFOND_MOIS);
  const mensuel = equivalentMensuel(loyer, periodicite);
  return {
    equivalentMensuel: mensuel,
    moisDemandes: demandes,
    moisRetenus: retenus,
    plafond: mensuel * PLAFOND_MOIS,
    cautionCalculee: mensuel * retenus,
  };
}

export function penaliteResiliation(preavisJours: number, loyer: number) {
  return preavisJours < PREAVIS_MIN ? loyer : 0;
}

export function restitutionCaution(caution: number, reparations: number, edlSortieValide: boolean) {
  if (!edlSortieValide) return { montantRetenu: 0, montantRestitue: 0 };
  const retenu = Math.min(Math.max(reparations, 0), Math.max(caution, 0));
  return { montantRetenu: retenu, montantRestitue: Math.max(caution - retenu, 0) };
}

export function contactsMasques(revele: boolean) {
  return !revele;
}
