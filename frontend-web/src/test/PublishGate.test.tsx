import { render, screen } from "@testing-library/react";
import { peutPublier } from "../regles-suite";

function PublishGate({ photos }: { photos: number }) {
  const ok = peutPublier(photos);
  return <button disabled={!ok}>{ok ? "Publier" : "Publier (3 photos min)"}</button>;
}

test("desactive Publier sous 3 photos", () => {
  render(<PublishGate photos={2} />);
  expect(screen.getByRole("button")).toBeDisabled();
});

test("active Publier a partir de 3 photos", () => {
  render(<PublishGate photos={3} />);
  expect(screen.getByRole("button")).toBeEnabled();
});
