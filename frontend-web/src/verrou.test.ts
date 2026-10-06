import { describe, expect, it } from "vitest";
import { verrouReservation } from "./verrou";

describe("verrouReservation", () => {
  it("decrit les trois etats", () => {
    expect(verrouReservation("EN_ATTENTE")).toContain("24 h");
    expect(verrouReservation("ACCEPTEE")).toContain("contrat");
    expect(verrouReservation("REFUSEE")).toContain("Ne bloque plus");
  });
});
