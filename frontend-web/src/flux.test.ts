import { describe, expect, it } from "vitest";
import { nouveaux } from "./flux";

describe("flux", () => {
  it("ne reprend que les messages non encore vus", () => {
    expect(nouveaux(["a"], ["a", "b"])).toEqual(["b"]);
    expect(nouveaux(["a", "b"], ["a", "b"])).toEqual([]);
  });
});
