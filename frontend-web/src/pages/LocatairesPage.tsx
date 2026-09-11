import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Dossier, locatairesApi } from "../locataires";

export function LocatairesPage() {
  const [items, setItems] = useState<Dossier[]>([]);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    locatairesApi.list().then(setItems).catch((e) => setError(e.message));
  }, []);
  return (
    <main className="mx-auto max-w-5xl p-8">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-primary">Locataires</h1>
        <Link className="rounded-md bg-primary px-4 py-2 text-sm text-white" to="/locataires/nouveau">+ Dossier</Link>
      </div>
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="space-y-3">
        {items.map((d) => (
          <Link key={d.id} to={`/locataires/${d.id}`} className="block rounded-lg bg-white p-4 shadow hover:bg-slate-50">
            <div className="flex justify-between">
              <div>
                <p className="font-medium">{d.prenom} {d.nom}</p>
                <p className="text-sm text-slate-600">{d.telephone ?? d.email ?? "—"}</p>
              </div>
              <span className="text-sm">{d.kycStatut}</span>
            </div>
          </Link>
        ))}
        {items.length === 0 && !error && <p className="text-sm text-slate-500">Aucun dossier.</p>}
      </div>
      <p className="mt-6 text-sm"><Link className="text-primary" to="/">Accueil</Link></p>
    </main>
  );
}
