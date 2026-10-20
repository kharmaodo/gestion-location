import { render, screen } from "@testing-library/react";
import { expect, test } from "vitest";
import { contactsMasques } from "../regles";

function ContactsGate({ revele, nom }: { revele: boolean; nom: string }) {
  if (contactsMasques(revele)) return <p>Contacts masques.</p>;
  return <p>Locataire : {nom}</p>;
}

test("masque les contacts tant que non reveles", () => {
  render(<ContactsGate revele={false} nom="Aminata Diop" />);
  expect(screen.getByText("Contacts masques.")).toBeInTheDocument();
});

test("affiche le nom quand revele", () => {
  render(<ContactsGate revele={true} nom="Aminata Diop" />);
  expect(screen.getByText("Locataire : Aminata Diop")).toBeInTheDocument();
});
