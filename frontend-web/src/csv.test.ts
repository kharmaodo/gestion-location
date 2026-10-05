import { describe, expect, it } from "vitest";
import { csvCell } from "./csv";

describe("csvCell", () => {
  it("laisse un texte simple", () => {
    expect(csvCell("Dakar")).toBe("Dakar");
  });
  it("echappe les guillemets", () => {
    expect(csvCell('a"b')).toBe('"a""b"');
  });
});
