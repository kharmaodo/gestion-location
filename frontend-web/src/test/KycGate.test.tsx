import { render, screen } from "@testing-library/react";
import { expect, test } from "vitest";
import { decisionKycValide } from "../regles-suite";

function KycGate({ statut }: { statut: string }) {
  return <button disabled={!decisionKycValide(statut)}>{statut}</button>;
}

test("accepte VALIDE et REJETE", () => {
  render(<KycGate statut="VALIDE" />);
  expect(screen.getByRole("button", { name: "VALIDE" })).toBeEnabled();
});

test("refuse un statut inconnu", () => {
  render(<KycGate statut="FERME" />);
  expect(screen.getByRole("button", { name: "FERME" })).toBeDisabled();
});
