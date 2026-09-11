import { FormEvent, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getAccessToken } from "../api";

type Msg = { id: string; auteurId: string; corps: string; creeLe: string };
type Conv = { id: string; messages?: Msg[] };

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

export function ConversationDetailPage() {
  const { id } = useParams();
  const [conv, setConv] = useState<Conv | null>(null);
  const [corps, setCorps] = useState("");
  const [error, setError] = useState<string | null>(null);

  async function load() {
    if (!id) return;
    try {
      setConv(await call(`/api/v1/conversations/${id}`));
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur");
    }
  }
  useEffect(() => { load(); }, [id]);

  async function send(e: FormEvent) {
    e.preventDefault();
    if (!id) return;
    try {
      await call(`/api/v1/conversations/${id}/messages`, { method: "POST", body: JSON.stringify({ corps }) });
      setCorps("");
      load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }

  if (!conv) return <p className="p-8">{error ?? "Chargement..."}</p>;
  return (
    <main className="mx-auto max-w-2xl p-8">
      <Link className="text-sm text-primary" to="/messages">Messages</Link>
      <div className="mt-4 space-y-2">
        {(conv.messages ?? []).map((m) => (
          <div key={m.id} className="rounded-lg bg-white p-3 shadow text-sm">
            <p>{m.corps}</p>
            <p className="text-xs text-slate-500">{m.auteurId} · {m.creeLe}</p>
          </div>
        ))}
      </div>
      <form className="mt-4 flex gap-2" onSubmit={send}>
        <input className="flex-1 rounded-md border px-3 py-2" value={corps} onChange={(e) => setCorps(e.target.value)} required />
        <button className="rounded-md bg-primary px-3 py-2 text-white">Envoyer</button>
      </form>
      {error && <p className="mt-2 text-sm text-red-600">{error}</p>}
    </main>
  );
}
