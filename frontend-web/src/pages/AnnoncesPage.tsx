import { FormEvent, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Annonce, vitrineApi } from "../vitrine";

export function AnnoncesPage() {
  const [ville, setVille] = useState("");
  const [items, setItems] = useState<Annonce[]>([]);
  const [error, setError] = useState<string | null>(null);
  async function load(e?: FormEvent) {
    e?.preventDefault();
    try {
      setItems(await vitrineApi.list(ville || undefined));
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }
  useEffect(() => { load(); }, []);
  return (
    <main className="mx-auto max-w-5xl p-8">
      <h1 className="mb-4 text-2xl font-semibold text-primary">Annonces</h1>
      <form className="mb-6 flex gap-2" onSubmit={load}>
        <input className="flex-1 rounded-md border px-3 py-2" placeholder="Ville" value={ville} onChange={(e) => setVille(e.target.value)} />
        <button className="rounded-md bg-primary px-4 py-2 text-white">Filtrer</button>
      </form>
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="grid gap-3 md:grid-cols-2">
        {items.map((a) => (
          <Link key={a.uniteId} to={`/annonces/${a.uniteId}`} className="rounded-lg bg-white p-4 shadow hover:bg-slate-50">
            <p className="font-medium">{a.libelle} · {a.designationBien}</p>
            <p className="text-sm text-slate-600">{a.ville} · {a.type} · {a.loyer} {a.devise} / {a.periodicite}</p>
          </Link>
        ))}
      </div>
      {items.length === 0 && !error && <p className="text-sm text-slate-500">Aucune annonce publiee.</p>}
    </main>
  );
}
