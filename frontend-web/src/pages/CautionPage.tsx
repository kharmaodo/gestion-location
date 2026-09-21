import { FormEvent, useState } from "react";

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

type Simu = {
  loyer: number;
  periodicite: string;
  equivalentMensuel: number;
  moisDemandes: number;
  moisRetenus: number;
  plafond: number;
  cautionCalculee: number;
  devise: string;
  regle: string;
};

export function CautionPage() {
  const [loyer, setLoyer] = useState("85000");
  const [periodicite, setPeriodicite] = useState("MENSUEL");
  const [mois, setMois] = useState("3");
  const [result, setResult] = useState<Simu | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    const qs = new URLSearchParams({
      loyer,
      periodicite,
      mois,
    });
    const res = await fetch(`${API}/api/v1/public/caution?${qs}`);
    const body = await res.json().catch(() => ({}));
    if (!res.ok) {
      setResult(null);
      setError(body.detail ?? "Simulation impossible");
      return;
    }
    setResult(body as Simu);
  }

  return (
    <main className="mx-auto max-w-xl p-8">
      <h1 className="mb-6 text-2xl font-semibold text-primary">Simulateur de caution</h1>
      <form className="space-y-3 rounded-lg bg-white p-4 shadow" onSubmit={onSubmit}>
        <label className="block text-sm">Loyer
          <input className="mt-1 w-full rounded-md border px-3 py-2" value={loyer} onChange={(e) => setLoyer(e.target.value)} />
        </label>
        <label className="block text-sm">Periodicite
          <select className="mt-1 w-full rounded-md border px-3 py-2" value={periodicite} onChange={(e) => setPeriodicite(e.target.value)}>
            <option value="MENSUEL">Mensuel</option>
            <option value="HEBDOMADAIRE">Hebdomadaire</option>
            <option value="JOURNALIER">Journalier</option>
          </select>
        </label>
        <label className="block text-sm">Mois demandes
          <input className="mt-1 w-full rounded-md border px-3 py-2" value={mois} onChange={(e) => setMois(e.target.value)} />
        </label>
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button className="rounded-md bg-primary px-4 py-2 text-sm text-white">Calculer</button>
      </form>
      {result && (
        <section className="mt-6 space-y-2 rounded-lg bg-white p-4 shadow text-sm">
          <p>Equivalent mensuel : <strong>{result.equivalentMensuel} {result.devise}</strong></p>
          <p>Mois retenus : <strong>{result.moisRetenus}</strong> / {result.moisDemandes} demandes</p>
          <p>Plafond : <strong>{result.plafond} {result.devise}</strong></p>
          <p className="text-lg">Caution : <strong>{result.cautionCalculee} {result.devise}</strong></p>
          <p className="text-slate-500">{result.regle}</p>
        </section>
      )}
    </main>
  );
}
