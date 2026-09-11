import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getAccessToken } from "../api";

type Paiement = { id: string; montant: number; mode: string; recuNumero: string; payeLe: string };
type Echeance = {
  id: string;
  contratId: string;
  periodeDebut: string;
  periodeFin: string;
  montant: number;
  devise: string;
  statut: string;
  paiements?: Paiement[];
};

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

async function call<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${getAccessToken()}`,
    },
  });
  const body = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(body.detail ?? "Erreur API");
  return body as T;
}

export function LoyersPage() {
  const [items, setItems] = useState<Echeance[]>([]);
  const [error, setError] = useState<string | null>(null);

  async function load() {
    try {
      setItems(await call("/api/v1/loyers"));
      setError(null);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur");
    }
  }
  useEffect(() => { load(); }, []);

  async function generer() {
    try {
      setItems(await call("/api/v1/loyers/generation", { method: "POST" }));
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur");
    }
  }

  async function payer(id: string, montant: number) {
    try {
      await call(`/api/v1/loyers/${id}/paiements`, {
        method: "POST",
        body: JSON.stringify({ montant, mode: "ESPECES" }),
      });
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur");
    }
  }

  return (
    <main className="mx-auto max-w-4xl p-8">
      <div className="mb-6 flex justify-between">
        <h1 className="text-2xl font-semibold text-primary">Loyers</h1>
        <button className="rounded-md bg-primary px-4 py-2 text-sm text-white" onClick={generer}>Generer les echeances</button>
      </div>
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="space-y-3">
        {items.map((e) => (
          <div key={e.id} className="rounded-lg bg-white p-4 shadow">
            <div className="flex justify-between">
              <div>
                <p className="font-medium">{e.periodeDebut} → {e.periodeFin}</p>
                <p className="text-sm text-slate-600">{e.montant} {e.devise} · {e.statut}</p>
              </div>
              {e.statut !== "PAYEE" && (
                <button className="rounded-md border px-3 py-1 text-sm" onClick={() => payer(e.id, e.montant)}>Encaisser</button>
              )}
            </div>
            <ul className="mt-2 text-sm text-slate-600">
              {(e.paiements ?? []).map((p) => (
                <li key={p.id}>Recu {p.recuNumero} · {p.montant} · {p.mode}</li>
              ))}
            </ul>
          </div>
        ))}
      </div>
      <p className="mt-6 text-sm"><Link className="text-primary" to="/">Accueil</Link></p>
    </main>
  );
}
