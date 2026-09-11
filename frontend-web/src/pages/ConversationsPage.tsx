import { FormEvent, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getAccessToken } from "../api";

type Conversation = {
  id: string;
  participantA: string;
  participantB: string;
  uniteId?: string;
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

export function ConversationsPage() {
  const [items, setItems] = useState<Conversation[]>([]);
  const [dest, setDest] = useState("");
  const [error, setError] = useState<string | null>(null);

  async function load() {
    try {
      setItems(await call("/api/v1/conversations"));
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

  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="mb-4 text-2xl font-semibold text-primary">Messages</h1>
      {error && <p className="text-sm text-red-600">{error}</p>}
      <form className="mb-6 flex gap-2" onSubmit={ouvrir}>
        <input className="flex-1 rounded-md border px-3 py-2 text-sm" placeholder="UUID destinataire" value={dest} onChange={(e) => setDest(e.target.value)} required />
        <button className="rounded-md bg-primary px-3 py-2 text-sm text-white">Nouvelle conversation</button>
      </form>
      <div className="space-y-2">
        {items.map((c) => (
          <Link key={c.id} to={`/messages/${c.id}`} className="block rounded-lg bg-white p-4 shadow">
            <p className="text-sm">{c.participantA} ↔ {c.participantB}</p>
          </Link>
        ))}
      </div>
      <p className="mt-6 text-sm"><Link className="text-primary" to="/">Accueil</Link></p>
    </main>
  );
}
