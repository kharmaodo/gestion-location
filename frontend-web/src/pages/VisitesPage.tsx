import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getAccessToken } from "../api";

type Visite = {
  id: string;
  nom: string;
  telephone?: string;
  creneau: string;
  statut: string;
};

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

async function call<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API}${path}`, {
    ...init,
    headers: { "Content-Type": "application/json", Authorization: `Bearer ${getAccessToken()}` },
  });
  const body = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(body.detail ?? "Erreur API");
  return body as T;
}

export function VisitesPage() {
  const [items, setItems] = useState<Visite[]>([]);
  const [error, setError] = useState<string | null>(null);

  async function load() {
    try { setItems(await call("/api/v1/visites")); } catch (e) { setError(e instanceof Error ? e.message : "Erreur"); }
  }
  useEffect(() => { load(); }, []);

  async function statut(id: string, s: string) {
    try {
      await call(`/api/v1/visites/${id}/statut`, { method: "POST", body: JSON.stringify({ statut: s }) });
      load();
    } catch (e) { setError(e instanceof Error ? e.message : "Erreur"); }
  }

  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="mb-4 text-2xl font-semibold text-primary">Visites</h1>
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="space-y-3">
        {items.map((v) => (
          <div key={v.id} className="rounded-lg bg-white p-4 shadow">
            <p className="font-medium">{v.nom} · {v.statut}</p>
            <p className="text-sm text-slate-600">{v.creneau} {v.telephone ?? ""}</p>
            {v.statut === "DEMANDEE" && (
              <div className="mt-2 flex gap-2">
                <button className="rounded-md bg-primary px-3 py-1 text-sm text-white" onClick={() => statut(v.id, "CONFIRMEE")}>Confirmer</button>
                <button className="rounded-md border px-3 py-1 text-sm" onClick={() => statut(v.id, "ANNULEE")}>Annuler</button>
              </div>
            )}
          </div>
        ))}
      </div>
      <p className="mt-6 text-sm"><Link className="text-primary" to="/">Accueil</Link></p>
    </main>
  );
}
