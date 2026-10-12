export const PHOTOS_MIN = 3;

export function peutPublier(photos: number) {
  return photos >= PHOTOS_MIN;
}

export function statutApresPaiement(montant: number, deja: number, total: number) {
  const paye = deja + montant;
  if (paye >= total) return "PAYEE";
  if (paye > 0) return "PARTIEL";
  return "A_PAYER";
}

export function decisionKycValide(statut: string) {
  return statut === "VALIDE" || statut === "REJETE";
}

export function nonLues(items: { lu: boolean }[]) {
  return items.filter((n) => !n.lu).length;
}
