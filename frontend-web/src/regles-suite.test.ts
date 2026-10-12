import { describe, expect, it } from "vitest";
import { decisionKycValide, nonLues, peutPublier, statutApresPaiement } from "./regles-suite";

describe("regles suite", () => {
  it("refuse la publication sans 3 photos", () => {
    expect(peutPublier(2)).toBe(false);
    expect(peutPublier(3)).toBe(true);
  });

  it("passe l'echeance en partiel puis payee", () => {
    expect(statutApresPaiement(4000, 0, 10000)).toBe("PARTIEL");
    expect(statutApresPaiement(6000, 4000, 10000)).toBe("PAYEE");
  });

  it("n'accepte que VALIDE ou REJETE pour le KYC", () => {
    expect(decisionKycValide("VALIDE")).toBe(true);
    expect(decisionKycValide("REJETE")).toBe(true);
    expect(decisionKycValide("FERME")).toBe(false);
  });

  it("compte les notifications non lues", () => {
    expect(nonLues([{ lu: false }, { lu: true }, { lu: false }])).toBe(2);
  });
});
