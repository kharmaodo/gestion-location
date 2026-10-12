import { describe, expect, it } from "vitest";
import { contactsMasques, penaliteResiliation, restitutionCaution, simulerCaution } from "./regles";

describe("regles contrat", () => {
  it("plafonne la caution a 3 mois", () => {
    const simu = simulerCaution(85000, "MENSUEL", 5);
    expect(simu.moisRetenus).toBe(3);
    expect(simu.cautionCalculee).toBe(255000);
  });

  it("applique une periode de loyer si le preavis est inferieur a 30 jours", () => {
    expect(penaliteResiliation(10, 85000)).toBe(85000);
    expect(penaliteResiliation(30, 85000)).toBe(0);
  });

  it("ne restitue rien sans etat des lieux de sortie valide", () => {
    expect(restitutionCaution(255000, 10000, false)).toEqual({ montantRetenu: 0, montantRestitue: 0 });
    expect(restitutionCaution(255000, 10000, true)).toEqual({ montantRetenu: 10000, montantRestitue: 245000 });
  });

  it("masque les contacts tant que les deux n'ont pas signe", () => {
    expect(contactsMasques(false)).toBe(true);
    expect(contactsMasques(true)).toBe(false);
  });
});
