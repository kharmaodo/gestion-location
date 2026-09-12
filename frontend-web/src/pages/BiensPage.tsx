import { useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { Bien, biensApi } from "../biens";

export function BiensPage() {
  const [params] = useSearchParams();
  const q = (params.get("q") ?? "").toLowerCase();
  const [biens, setBiens] = useState<Bien[]>([]);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    biensApi.list().then(setBiens).catch((e) => setError(e.message));
  }, []);
  const filtered = useMemo(
    () => biens.filter((b) => !q || `${b.designation} ${b.ville ?? ""} ${b.adresse ?? ""}`.toLowerCase().includes(q)),
    [biens, q]
  );
  return (
    <main className="mx-auto max-w-5xl p-8">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-primary">Mes biens</h1>
        <Link className="rounded-md bg-primary px-4 py-2 text-sm text-white" to="/biens/nouveau">+ Bien</Link>
      </div>
      {q && <p className="mb-3 text-sm text-slate-500">Filtre : {q}</p>}
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="space-y-3">
        {filtered.map((b) => (
          <Link key={b.id} to={`/biens/${b.id}`} className="block rounded-lg bg-white p-4 shadow hover:bg-slate-50">
            <div className="flex justify-between">
              <div>
                <p className="font-medium">{b.designation}</p>
                <p className="text-sm text-slate-600">{b.type} · {b.ville ?? b.adresse ?? "—"}</p>
              </div>
              <p className="text-sm">{b.unitesLibres} libres / {b.unites} unites</p>
            </div>
          </Link>
        ))}
        {filtered.length === 0 && !error && <p className="text-sm text-slate-500">Aucun bien.</p>}
      </div>
    </main>
  );
}
