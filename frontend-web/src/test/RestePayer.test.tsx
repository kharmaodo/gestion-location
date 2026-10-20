import { render, screen } from "@testing-library/react";
import { expect, test } from "vitest";
import { statutApresPaiement } from "../regles-suite";

function RestePayer({ deja, total }: { deja: number; total: number }) {
  const statut = statutApresPaiement(0, deja, total);
  const reste = Math.max(total - deja, 0);
  return <p>{statut} · reste {reste}</p>;
}

test("affiche PARTIEL et le reste", () => {
  render(<RestePayer deja={4000} total={10000} />);
  expect(screen.getByText("PARTIEL · reste 6000")).toBeInTheDocument();
});

test("affiche PAYEE quand tout est regle", () => {
  render(<RestePayer deja={10000} total={10000} />);
  expect(screen.getByText("PAYEE · reste 0")).toBeInTheDocument();
});
