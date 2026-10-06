import { describe, expect, it } from "vitest";
import { gardeReservation, verrouReservation } from "./verrou";

describe("verrouReservation", () => {
  it("decrit les etats", () => {
    expect(verrouReservation("EN_ATTENTE")).toContain("24 h");
    expect(verrouReservation("ACCEPTEE")).toContain("contrat");
    expect(verrouReservation("EXPIREE")).toContain("libre");
    expect(verrouReservation("REFUSEE")).toContain("Ne bloque plus");
  });
  it("filtre les expirees", () => {
    expect(gardeReservation("EXPIREE", "EXPIREE")).toBe(true);
    expect(gardeReservation("EN_ATTENTE", "EXPIREE")).toBe(false);
    expect(gardeReservation("EXPIREE", "TOUS")).toBe(true);
  });
});
