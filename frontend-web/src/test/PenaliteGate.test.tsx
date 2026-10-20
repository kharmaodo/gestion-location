import { render, screen } from "@testing-library/react";
import { expect, test } from "vitest";
import { penaliteResiliation } from "../regles";

function PenaliteGate({ jours, loyer }: { jours: number; loyer: number }) {
  const p = penaliteResiliation(jours, loyer);
  return <p>Penalite estimee : {p}</p>;
}

test("applique une periode si preavis inferieur a 30 jours", () => {
  render(<PenaliteGate jours={15} loyer={85000} />);
  expect(screen.getByText("Penalite estimee : 85000")).toBeInTheDocument();
});

test("pas de penalite si preavis respecte", () => {
  render(<PenaliteGate jours={30} loyer={85000} />);
  expect(screen.getByText("Penalite estimee : 0")).toBeInTheDocument();
});
