import { describe, expect, it } from "vitest";
import { cibleRecherche, liensPourRole } from "./menu";

describe("menu", () => {
  it("adapte le menu au role sans doublon", () => {
    const proprio = liensPourRole(true).map((l) => l.to);
    const locataire = liensPourRole(false).map((l) => l.to);
    expect(proprio).toContain("/biens");
    expect(proprio).toContain("/loyers");
    expect(locataire).not.toContain("/biens");
    expect(locataire).not.toContain("/loyers");
    expect(locataire.filter((to) => to === "/securite")).toHaveLength(1);
  });

  it("envoie la recherche vers le bon ecran", () => {
    expect(cibleRecherche("/annonces/1", false)).toBe("/annonces");
    expect(cibleRecherche("/contrats/1", true)).toBe("/contrats");
    expect(cibleRecherche("/locataires", true)).toBe("/locataires");
    expect(cibleRecherche("/", true)).toBe("/biens");
    expect(cibleRecherche("/", false)).toBe("/annonces");
  });
});
