import { describe, expect, it } from "vitest";
import { linksFor, translate } from "./nav";

describe("menu", () => {
  it("cache les biens au locataire et n'a pas de doublon Securite", () => {
    const links = linksFor(["LOCATAIRE"]);
    expect(links.map((l) => l.to)).not.toContain("/biens");
    expect(links.filter((l) => l.key === "nav.securite")).toHaveLength(1);
  });

  it("donne les loyers au proprietaire", () => {
    expect(linksFor(["PROPRIETAIRE"]).map((l) => l.to)).toContain("/loyers");
  });

  it("garde la cle si la traduction manque", () => {
    expect(translate({}, "nav.canaux")).toBe("nav.canaux");
    expect(translate({ "nav.canaux": "Canaux" }, "nav.canaux")).toBe("Canaux");
  });
});
