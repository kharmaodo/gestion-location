import { useEffect, useState } from "react";
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
  const [error, setError] = useState<string | null>(null);
  async function load() {
    const res = await fetch(`${API}/api/v1/reservations`, {
      headers: { Authorization: `Bearer ${getAccessToken()}` },
    });
    const body = await res.json().catch(() => []);
    if (!res.ok) setError(body.detail ?? "Erreur");
    else setItems(body);
  }
  useEffect(() => { load(); }, []);
  async function decide(id: string, statut: string) {
    await fetch(`${API}/api/v1/reservations/${id}/decision`, {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${getAccessToken()}` },
      body: JSON.stringify({ statut }),
    });
    load();
  }
  return (
    <main className="mx-auto max-w-4xl p-8">
      <h1 className="mb-6 text-2xl font-semibold text-primary">Reservations</h1>
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="space-y-3">
        {items.map((r) => (
          <div key={r.id} className="rounded-lg bg-white p-4 shadow">
            <p className="font-medium">{r.prenom} {r.nom} · {r.telephone}</p>
            <p className="text-sm">{r.dateDebut} → {r.dateFin} · {r.statut}</p>
            {r.statut === "EN_ATTENTE" && (
              <div className="mt-2 flex gap-2">
                <button className="rounded-md bg-emerald-700 px-3 py-1 text-sm text-white" onClick={() => decide(r.id, "ACCEPTEE")}>Accepter</button>
                <button className="rounded-md bg-red-700 px-3 py-1 text-sm text-white" onClick={() => decide(r.id, "REFUSEE")}>Refuser</button>
              </div>
            )}
          </div>
        ))}
      </div>
      <p className="mt-6 text-sm"><Link className="text-primary" to="/">Accueil</Link></p>
    </main>
  );
}
