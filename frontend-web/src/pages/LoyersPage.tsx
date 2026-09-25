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
type Relance = { id?: string; echeanceId?: string; canal?: string; statut?: string; message?: string };
type Intention = { id: string; fournisseur: string; statut: string; checkoutUrl?: string };

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
  const [relances, setRelances] = useState<Relance[]>([]);
  const [mode, setMode] = useState("ESPECES");
  const [error, setError] = useState<string | null>(null);

  async function load() {
    try {
      setItems(await call("/api/v1/loyers"));
      setRelances(await call("/api/v1/loyers/relances").catch(() => []));
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

  async function relancer() {
    try {
      setRelances(await call("/api/v1/loyers/relances", { method: "POST" }));
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur");
    }
  }

  async function payer(id: string, montant: number) {
    try {
      await call(`/api/v1/loyers/${id}/paiements`, {
        method: "POST",
        body: JSON.stringify({ montant, mode }),
      });
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur");
    }
  }

  async function payerEnLigne(id: string) {
    const fournisseur = ["WAVE", "ORANGE_MONEY", "CARTE"].includes(mode) ? mode : "WAVE";
    try {
      const intention = await call<Intention>(`/api/v1/loyers/${id}/paiement-en-ligne`, {
        method: "POST",
        body: JSON.stringify({ fournisseur }),
      });
      await call("/api/v1/public/paiements/webhook", {
        method: "POST",
        body: JSON.stringify({ intentionId: intention.id, statut: "REUSSI" }),
      });
      load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur paiement en ligne");
    }
  }

  return (
    <main className="mx-auto max-w-4xl p-8">
      <div className="mb-6 flex flex-wrap items-center justify-between gap-2">
        <h1 className="text-2xl font-semibold text-primary">Loyers</h1>
        <div className="flex gap-2">
          <button className="rounded-md border px-4 py-2 text-sm" onClick={relancer}>Relancer impayes</button>
          <button className="rounded-md bg-primary px-4 py-2 text-sm text-white" onClick={generer}>Generer les echeances</button>
        </div>
      </div>
      {error && <p className="text-sm text-red-600">{error}</p>}
      <label className="mb-3 block text-sm">Mode d'encaissement
        <select className="ml-2 rounded-md border px-2 py-1" value={mode} onChange={(e) => setMode(e.target.value)}>
          <option value="ESPECES">Especes</option>
          <option value="VIREMENT">Virement</option>
          <option value="WAVE">Wave</option>
          <option value="ORANGE_MONEY">Orange Money</option>
          <option value="CARTE">Carte</option>
        </select>
      </label>
      <div className="space-y-3">
        {items.map((e) => (
          <div key={e.id} className="rounded-lg bg-white p-4 shadow">
            <div className="flex flex-wrap items-center justify-between gap-2">
              <div>
                <p className="font-medium">{e.periodeDebut} → {e.periodeFin}</p>
                <p className="text-sm text-slate-600">{e.montant} {e.devise} · {e.statut}</p>
              </div>
              {e.statut !== "PAYEE" && (
                <div className="flex gap-2">
                  <button className="rounded-md border px-3 py-1 text-sm" onClick={() => payer(e.id, e.montant)}>Encaisser</button>
                  <button className="rounded-md bg-primary px-3 py-1 text-sm text-white" onClick={() => payerEnLigne(e.id)}>Payer en ligne</button>
                </div>
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
      {relances.length > 0 && (
        <section className="mt-6 rounded-lg bg-white p-4 text-sm shadow">
          <h2 className="mb-2 font-medium">Relances</h2>
          <ul className="space-y-1">
            {relances.map((r, i) => (
              <li key={r.id ?? i}>{r.canal ?? "MAIL"} · {r.statut ?? "ENVOYEE"} {r.message ? `· ${r.message}` : ""}</li>
            ))}
          </ul>
        </section>
      )}
      <p className="mt-6 text-sm"><Link className="text-primary" to="/">Accueil</Link></p>
    </main>
  );
}
