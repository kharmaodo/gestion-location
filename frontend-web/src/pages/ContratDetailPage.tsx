import { FormEvent, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Contrat, contratsApi } from "../contrats";

export function ContratDetailPage() {
  const { id } = useParams();
  const [contrat, setContrat] = useState<Contrat | null>(null);
  const [motif, setMotif] = useState("Changement de periodicite");
  const [periodicite, setPeriodicite] = useState("MENSUEL");
  const [loyer, setLoyer] = useState("");
  const [dateEffet, setDateEffet] = useState("");
  const [error, setError] = useState<string | null>(null);

  function reload() {
    if (id) contratsApi.get(id).then(setContrat).catch((e) => setError(e.message));
  }
  useEffect(reload, [id]);

  async function activer() {
    if (!id) return;
    try { await contratsApi.activer(id); reload(); } catch (e) { setError(e instanceof Error ? e.message : "Erreur"); }
  }
  async function resilier() {
    if (!id) return;
    try { await contratsApi.resilier(id); reload(); } catch (e) { setError(e instanceof Error ? e.message : "Erreur"); }
  }
  async function avenant(e: FormEvent) {
    e.preventDefault();
    if (!id) return;
    try {
      await contratsApi.avenant(id, { motif, periodicite, loyer: loyer ? Number(loyer) : null, dateEffet });
      reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }

  if (!contrat) return <p className="p-8">{error ?? "Chargement..."}</p>;
  return (
    <main className="mx-auto max-w-2xl p-8">
      <Link className="text-sm text-primary" to="/contrats">Contrats</Link>
      <h1 className="mt-2 text-2xl font-semibold text-primary">Contrat {contrat.statut}</h1>
      <p className="text-sm">{contrat.loyer} {contrat.devise} / {contrat.periodicite} · {contrat.dateDebut}</p>
      {error && <p className="mt-2 text-sm text-red-600">{error}</p>}
      <div className="mt-4 flex gap-2">
        {contrat.statut === "BROUILLON" && <button className="rounded-md bg-primary px-3 py-2 text-sm text-white" onClick={activer}>Activer</button>}
        {contrat.statut === "ACTIF" && <button className="rounded-md border px-3 py-2 text-sm" onClick={resilier}>Resilier</button>}
      </div>
      <ul className="mt-6 space-y-2 text-sm">
        {(contrat.avenants ?? []).map((a) => (
          <li key={a.id} className="rounded-lg bg-white p-3 shadow">{a.dateEffet} — {a.motif} {a.periodicite ?? ""} {a.loyer ?? ""}</li>
        ))}
      </ul>
      {contrat.statut === "ACTIF" && (
        <form className="mt-6 space-y-3 rounded-lg bg-white p-4 shadow" onSubmit={avenant}>
          <h2 className="font-medium">Avenant</h2>
          <input className="w-full rounded-md border px-3 py-2" value={motif} onChange={(e) => setMotif(e.target.value)} required />
          <select className="w-full rounded-md border px-3 py-2" value={periodicite} onChange={(e) => setPeriodicite(e.target.value)}>
            <option value="JOURNALIER">Journalier</option>
            <option value="HEBDOMADAIRE">Hebdomadaire</option>
            <option value="MENSUEL">Mensuel</option>
          </select>
          <input className="w-full rounded-md border px-3 py-2" placeholder="Nouveau loyer (optionnel)" value={loyer} onChange={(e) => setLoyer(e.target.value)} />
          <input type="date" className="w-full rounded-md border px-3 py-2" value={dateEffet} onChange={(e) => setDateEffet(e.target.value)} required />
          <button className="rounded-md bg-primary px-4 py-2 text-sm text-white">Enregistrer avenant</button>
        </form>
      )}
    </main>
  );
}
