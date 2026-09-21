import { useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { Contrat, contratsApi } from "../contrats";

export function ContratsPage() {
  const [params] = useSearchParams();
  const q = (params.get("q") ?? "").toLowerCase();
  const [items, setItems] = useState<Contrat[]>([]);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    contratsApi.list().then(setItems).catch((e) => setError(e.message));
  }, []);
  const filtered = useMemo(
    () =>
      items.filter(
        (c) =>
          !q ||
          `${c.statut} ${c.periodicite} ${c.loyer} ${c.dateDebut} ${c.dateFin ?? ""}`
            .toLowerCase()
            .includes(q)
      ),
    [items, q]
  );
  return (
    <main className="mx-auto max-w-4xl p-8">
      <div className="mb-6 flex justify-between">
        <h1 className="text-2xl font-semibold text-primary">Contrats</h1>
        <Link className="rounded-md bg-primary px-4 py-2 text-sm text-white" to="/contrats/nouveau">+ Contrat</Link>
      </div>
      {q && <p className="mb-3 text-sm text-slate-500">Filtre : {q}</p>}
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="space-y-3">
        {filtered.map((c) => (
          <Link key={c.id} to={`/contrats/${c.id}`} className="block rounded-lg bg-white p-4 shadow">
            <p className="font-medium">{c.periodicite} · {c.loyer} {c.devise}</p>
            <p className="text-sm text-slate-600">{c.dateDebut} → {c.dateFin ?? "—"} · {c.statut}</p>
          </Link>
        ))}
        {filtered.length === 0 && !error && <p className="text-sm text-slate-500">Aucun contrat.</p>}
      </div>
    </main>
  );
}
