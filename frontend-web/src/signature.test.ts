import { describe, expect, it } from "vitest";
import { signatureLibelle } from "./signature";

describe("signatureLibelle", () => {
  it("decrit mock live et off", () => {
    expect(signatureLibelle("MOCK")).toBe("Signature mock");
    expect(signatureLibelle("LIVE")).toContain("non branchee");
    expect(signatureLibelle("OFF")).toContain("desactivee");
  });
});
