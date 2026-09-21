import { FormEvent, useEffect, useState } from "react";
import { Litige, litigesApi } from "../litiges";

export function LitigesPage() {
  const [items, setItems] = useState<Litige[]>([]);
  const [contratId, setContratId] = useState("");
  const [motif, setMotif] = useState("DEGRADATION");
  const [description, setDescription] = useState("");
  const [error, setError] = useState<string | null>(null);

  function reload() {
    litigesApi.list().then(setItems).catch((e) => setError(e.message));
  }
  useEffect(reload, []);

  async function ouvrir(e: FormEvent) {
    e.preventDefault();
    try {
      await litigesApi.ouvrir({ contratId, motif, description });
      setDescription("");
      reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }

  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="mb-6 text-2xl font-semibold text-primary">Litiges</h1>
      {error && <p className="mb-3 text-sm text-red-600">{error}</p>}
      <div className="space-y-2">
        {items.map((l) => (
          <div key={l.id} className="rounded-lg bg-white p-4 text-sm shadow">
            <p className="font-medium">{l.motif} · {l.statut}</p>
            <p className="text-slate-600">{l.description}</p>
            {l.decision && <p className="mt-1">Decision : {l.decision}</p>}
            {l.statut === "OUVERT" && (
              <div className="mt-2 flex gap-2">
                <button className="rounded-md border px-2 py-1 text-xs" onClick={() => litigesApi.decider(l.id, "RESOLU", "Accord").then(reload).catch((e) => setError(e.message))}>Resoudre</button>
                <button className="rounded-md border px-2 py-1 text-xs" onClick={() => litigesApi.decider(l.id, "REJETE", "Non fonde").then(reload).catch((e) => setError(e.message))}>Rejeter</button>
              </div>
            )}
          </div>
        ))}
        {items.length === 0 && <p className="text-sm text-slate-500">Aucun litige.</p>}
      </div>
      <form className="mt-6 space-y-3 rounded-lg bg-white p-4 shadow" onSubmit={ouvrir}>
        <h2 className="font-medium">Ouvrir un litige</h2>
        <input className="w-full rounded-md border px-3 py-2 text-sm" placeholder="UUID contrat" value={contratId} onChange={(e) => setContratId(e.target.value)} required />
        <select className="w-full rounded-md border px-3 py-2 text-sm" value={motif} onChange={(e) => setMotif(e.target.value)}>
          <option value="DEGRADATION">Degradation</option>
          <option value="IMPAYE">Impaye</option>
          <option value="NUISANCE">Nuisance</option>
          <option value="AUTRE">Autre</option>
        </select>
        <textarea className="w-full rounded-md border px-3 py-2 text-sm" placeholder="Description" value={description} onChange={(e) => setDescription(e.target.value)} required />
        <button className="rounded-md bg-primary px-4 py-2 text-sm text-white">Ouvrir</button>
      </form>
    </main>
  );
}
