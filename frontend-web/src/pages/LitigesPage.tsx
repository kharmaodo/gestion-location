import { FormEvent, useEffect, useState } from "react";
import { api } from "../api";
import { Contrat, contratsApi } from "../contrats";
import { Litige, litigesApi } from "../litiges";

export function LitigesPage() {
  const [items, setItems] = useState<Litige[]>([]);
  const [contrats, setContrats] = useState<Contrat[]>([]);
  const [contratId, setContratId] = useState("");
  const [motif, setMotif] = useState("DEGATS");
  const [description, setDescription] = useState("");
  const [roles, setRoles] = useState<string[]>([]);
  const [error, setError] = useState<string | null>(null);
  const proprio = roles.includes("PROPRIETAIRE");

  function reload() {
    litigesApi.list().then(setItems).catch((e) => setError(e.message));
    contratsApi.mes().then((list) => {
      setContrats(list);
      setContratId((cur) => cur || list[0]?.id || "");
    }).catch(() => setContrats([]));
  }
  useEffect(() => {
    api.me().then((m) => setRoles(m.roles)).catch(() => undefined);
    reload();
  }, []);

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
            {proprio && l.statut === "OUVERT" && (
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
        <select className="w-full rounded-md border px-3 py-2 text-sm" value={contratId} onChange={(e) => setContratId(e.target.value)} required>
          <option value="">{proprio ? "Choisir un contrat" : "Votre contrat"}</option>
          {contrats.map((c) => (
            <option key={c.id} value={c.id}>
              {c.statut} · {c.loyer} {c.devise} / {c.periodicite} · {c.dateDebut}
            </option>
          ))}
        </select>
        {contrats.length === 0 && (
          <p className="text-xs text-slate-500">
            {proprio
              ? "Aucun contrat. Creez-en un d'abord."
              : "Aucun contrat lie a votre compte locataire (dossier.utilisateur_id)."}
          </p>
        )}
        <select className="w-full rounded-md border px-3 py-2 text-sm" value={motif} onChange={(e) => setMotif(e.target.value)}>
          <option value="DEGATS">Degats</option>
          <option value="IMPAYE">Impaye</option>
          <option value="CAUTION">Caution</option>
          <option value="RESILIATION">Resiliation</option>
          <option value="AUTRE">Autre</option>
        </select>
        <textarea className="w-full rounded-md border px-3 py-2 text-sm" placeholder="Description" value={description} onChange={(e) => setDescription(e.target.value)} required />
        <button className="rounded-md bg-primary px-4 py-2 text-sm text-white" disabled={!contratId}>Ouvrir</button>
      </form>
    </main>
  );
}
