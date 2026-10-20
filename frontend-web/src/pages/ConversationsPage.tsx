import { FormEvent, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { getAccessToken } from "../api";

type Conversation = {
  id: string;
  participantA: string;
  participantB: string;
  uniteId?: string;
};

type Destinataire = {
  id: string;
  prenom: string;
  nom: string;
  email?: string;
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

function libelle(d: Destinataire): string {
  const nom = `${d.prenom} ${d.nom}`.trim();
  if (nom && d.email) return `${nom} · ${d.email}`;
  return nom || d.email || d.id;
}

export function ConversationsPage() {
  const [items, setItems] = useState<Conversation[]>([]);
  const [destinataires, setDestinataires] = useState<Destinataire[]>([]);
  const [dest, setDest] = useState("");
  const [error, setError] = useState<string | null>(null);
  const noms = useMemo(() => new Map(destinataires.map((d) => [d.id, libelle(d)])), [destinataires]);

  async function load() {
    try {
      const [cs, ds] = await Promise.all([
        call<Conversation[]>("/api/v1/conversations"),
        call<Destinataire[]>("/api/v1/conversations/destinataires"),
      ]);
      setItems(cs);
      setDestinataires(ds);
      setDest((prev) => prev || ds[0]?.id || "");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur");
    }
  }
  useEffect(() => { load(); }, []);

  async function ouvrir(e: FormEvent) {
    e.preventDefault();
    try {
      const c = await call<Conversation>("/api/v1/conversations", {
        method: "POST",
        body: JSON.stringify({ destinataireId: dest }),
      });
      window.location.href = `/messages/${c.id}`;
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }

  function nomDe(id: string): string {
    return noms.get(id) ?? id;
  }

  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="mb-4 text-2xl font-semibold text-primary">Messages</h1>
      {error && <p className="text-sm text-red-600">{error}</p>}
      <form className="mb-6 flex gap-2" onSubmit={ouvrir}>
        <select className="flex-1 rounded-md border px-3 py-2 text-sm" value={dest} onChange={(e) => setDest(e.target.value)} required>
          {destinataires.length === 0 && <option value="">Aucun destinataire</option>}
          {destinataires.map((d) => (
            <option key={d.id} value={d.id}>{libelle(d)}</option>
          ))}
        </select>
        <button className="rounded-md bg-primary px-3 py-2 text-sm text-white" disabled={!dest}>Nouvelle conversation</button>
      </form>
      <div className="space-y-2">
        {items.map((c) => (
          <Link key={c.id} to={`/messages/${c.id}`} className="block rounded-lg bg-white p-4 shadow">
            <p className="text-sm">{nomDe(c.participantA)} ↔ {nomDe(c.participantB)}</p>
          </Link>
        ))}
      </div>
      <p className="mt-6 text-sm"><Link className="text-primary" to="/">Accueil</Link></p>
    </main>
  );
}
