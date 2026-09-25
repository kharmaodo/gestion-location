import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { getAccessToken } from "../api";

type Resa = {
  id: string;
  uniteId: string;
  nom: string;
  prenom?: string;
  telephone?: string;
  dateDebut: string;
  dateFin: string;
  statut: string;
  message?: string;
};

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export function ReservationsPage() {
  const [items, setItems] = useState<Resa[]>([]);
  const [filtre, setFiltre] = useState("TOUS");
  const [error, setError] = useState<string | null>(null);

  async function load() {
    const res = await fetch(`${API}/api/v1/reservations`, {
      headers: { Authorization: `Bearer ${getAccessToken()}` },
    });
    const body = await res.json().catch(() => []);
    if (!res.ok) setError(body.detail ?? "Erreur");
    else {
      setError(null);
      setItems(body);
    }
  }
  useEffect(() => { load(); }, []);

  async function decide(id: string, statut: string) {
    const res = await fetch(`${API}/api/v1/reservations/${id}/decision`, {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${getAccessToken()}` },
      body: JSON.stringify({ statut }),
    });
    const body = await res.json().catch(() => ({}));
    if (!res.ok) setError(body.detail ?? "Decision impossible");
    else load();
  }

  const filtered = useMemo(
    () => items.filter((r) => filtre === "TOUS" || r.statut === filtre),
    [items, filtre]
  );

  return (
    <main className="mx-auto max-w-4xl p-8">
      <div className="mb-6 flex flex-wrap items-center justify-between gap-2">
        <h1 className="text-2xl font-semibold text-primary">Reservations</h1>
        <Link className="text-sm text-primary" to="/annonces">Voir les annonces</Link>
      </div>
      <div className="mb-4 flex flex-wrap gap-2 text-sm">
        {[
          ["TOUS", "Tous"],
          ["EN_ATTENTE", "En attente"],
          ["ACCEPTEE", "Acceptees"],
          ["REFUSEE", "Refusees"],
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
        {filtered.map((r) => (
          <div key={r.id} className="rounded-lg bg-white p-4 shadow">
            <div className="flex items-start justify-between gap-2">
              <div>
                <p className="font-medium">{r.prenom} {r.nom} · {r.telephone ?? "—"}</p>
                <p className="text-sm">{r.dateDebut} → {r.dateFin}</p>
                {r.message && <p className="mt-1 text-sm text-slate-600">{r.message}</p>}
              </div>
              <span className="rounded bg-slate-100 px-2 py-0.5 text-xs">{r.statut}</span>
            </div>
            {r.statut === "EN_ATTENTE" && (
              <div className="mt-3 flex gap-2">
                <button className="rounded-md bg-emerald-700 px-3 py-1 text-sm text-white" onClick={() => decide(r.id, "ACCEPTEE")}>Accepter</button>
                <button className="rounded-md bg-red-700 px-3 py-1 text-sm text-white" onClick={() => decide(r.id, "REFUSEE")}>Refuser</button>
              </div>
            )}
            {r.statut === "ACCEPTEE" && (
              <Link
                className="mt-3 inline-block rounded-md bg-primary px-3 py-1 text-sm text-white"
                to={`/contrats/nouveau?reservationId=${r.id}&uniteId=${r.uniteId}&dateDebut=${r.dateDebut}`}
              >
                Creer le contrat
              </Link>
            )}
          </div>
        ))}
        {filtered.length === 0 && !error && (
          <p className="rounded-lg bg-white p-4 text-sm text-slate-500 shadow">
            Aucune reservation. Elles arrivent depuis le formulaire public d’une annonce.
          </p>
        )}
      </div>
    </main>
  );
}
