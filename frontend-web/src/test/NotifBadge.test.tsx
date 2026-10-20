import { render, screen } from "@testing-library/react";
import { expect, test } from "vitest";
import { nonLues } from "../regles-suite";

function NotifBadge({ items }: { items: { lu: boolean }[] }) {
  const n = nonLues(items);
  return <span>{n > 0 ? `Notifications (${n})` : "Notifications"}</span>;
}

test("affiche le compteur si non lues", () => {
  render(<NotifBadge items={[{ lu: false }, { lu: true }, { lu: false }]} />);
  expect(screen.getByText("Notifications (2)")).toBeInTheDocument();
});

test("n'affiche pas de compteur si tout est lu", () => {
  render(<NotifBadge items={[{ lu: true }]} />);
  expect(screen.getByText("Notifications")).toBeInTheDocument();
});
