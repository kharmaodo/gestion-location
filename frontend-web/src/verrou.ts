export function verrouReservation(statut: string) {
  if (statut === "EN_ATTENTE") return "Bloque le creneau 24 h, puis se libere.";
  if (statut === "ACCEPTEE") return "Bloque le creneau jusqu'au contrat.";
  return "Ne bloque plus le creneau.";
}
