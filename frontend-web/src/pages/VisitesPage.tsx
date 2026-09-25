import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { getAccessToken } from "../api";

type Visite = {
  id: string;
  uniteId?: string;
  nom: string;
  telephone?: string;
  email?: string;
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
  const [filtre, setFiltre] = useState("TOUS");
  const [error, setError] = useState<string | null>(null);

  async function load() {
    try {
      setItems(await call("/api/v1/visites"));
      setError(null);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur");
    }
  }
  useEffect(() => { load(); }, []);

  async function statut(id: string, s: string) {
    try {
      await call(`/api/v1/visites/${id}/statut`, { method: "POST", body: JSON.stringify({ statut: s }) });
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur");
    }
  }

  const filtered = useMemo(
    () => items.filter((v) => filtre === "TOUS" || v.statut === filtre),
    [items, filtre]
  );

  return (
    <main className="mx-auto max-w-3xl p-8">
      <div className="mb-4 flex flex-wrap items-center justify-between gap-2">
        <h1 className="text-2xl font-semibold text-primary">Visites</h1>
        <Link className="text-sm text-primary" to="/annonces">Demander depuis une annonce</Link>
      </div>
      <div className="mb-4 flex flex-wrap gap-2 text-sm">
        {[
          ["TOUS", "Tous"],
          ["DEMANDEE", "Demandees"],
          ["CONFIRMEE", "Confirmees"],
          ["EFFECTUEE", "Effectuees"],
          ["ANNULEE", "Annulees"],
        ].map(([v, l]) => (
          <button
            key={v}
            className={`rounded-md border px-3 py-1 ${filtre === v ? "bg-primary text-white" : "bg-white"}`}
            onClick={() => setFiltre(v)}
          >
            {l}
          </button>
        ))}
      </div>
      {error && <p className="mb-3 text-sm text-red-600">{error}</p>}
      <div className="space-y-3">
        {filtered.map((v) => (
          <div key={v.id} className="rounded-lg bg-white p-4 shadow">
            <div className="flex items-start justify-between gap-2">
              <div>
                <p className="font-medium">{v.nom}</p>
                <p className="text-sm text-slate-600">{new Date(v.creneau).toLocaleString()}</p>
                <p className="text-sm text-slate-600">{v.telephone ?? "—"} · {v.email ?? "—"}</p>
                {v.uniteId && (
                  <Link className="text-xs text-primary" to={`/annonces/${v.uniteId}`}>Unite {v.uniteId.slice(0, 8)}</Link>
                )}
              </div>
              <span className="rounded bg-slate-100 px-2 py-0.5 text-xs">{v.statut}</span>
            </div>
            {v.statut === "DEMANDEE" && (
              <div className="mt-3 flex gap-2">
                <button className="rounded-md bg-primary px-3 py-1 text-sm text-white" onClick={() => statut(v.id, "CONFIRMEE")}>Confirmer</button>
                <button className="rounded-md border px-3 py-1 text-sm" onClick={() => statut(v.id, "ANNULEE")}>Annuler</button>
              </div>
            )}
            {v.statut === "CONFIRMEE" && (
              <div className="mt-3 flex gap-2">
                <button className="rounded-md bg-primary px-3 py-1 text-sm text-white" onClick={() => statut(v.id, "EFFECTUEE")}>Marquer effectuee</button>
                <button className="rounded-md border px-3 py-1 text-sm" onClick={() => statut(v.id, "ANNULEE")}>Annuler</button>
              </div>
            )}
          </div>
        ))}
        {filtered.length === 0 && !error && (
          <p className="rounded-lg bg-white p-4 text-sm text-slate-500 shadow">
            Aucune visite. Les demandes partent depuis une annonce publiee.
          </p>
        )}
      </div>
    </main>
  );
}
