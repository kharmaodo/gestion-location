import { FormEvent, useEffect, useState } from "react";
import { getAccessToken } from "../api";

type Journal = { canal: string; mode: string; cible: string; message: string; at: string };
type Statut = { sms: string; fcm: string; psp: string; journal: Journal[] };

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

export function CanauxPage() {
  const [statut, setStatut] = useState<Statut | null>(null);
  const [telephone, setTelephone] = useState("770000000");
  const [token, setToken] = useState("fcm-device");
  const [fournisseur, setFournisseur] = useState("WAVE");
  const [message, setMessage] = useState("Rappel loyer");
  const [error, setError] = useState<string | null>(null);

  async function load() {
    try {
      setStatut(await call("/api/v1/canaux"));
      setError(null);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur");
    }
  }
  useEffect(() => { load(); }, []);

  async function envoyer(e: FormEvent, path: string, payload: unknown) {
    e.preventDefault();
    try {
      await call(path, { method: "POST", body: JSON.stringify(payload) });
      load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }

  function badge(mode?: string) {
    if (mode === "LIVE") return "LIVE — cles prestataire requises";
    if (mode === "OFF") return "OFF";
    return "MOCK";
  }

  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="mb-2 text-2xl font-semibold text-primary">Canaux prestataires</h1>
      <p className="mb-4 text-sm text-slate-500">Flags SMS_MODE, FCM_MODE, PSP_MODE. MOCK journalise. LIVE sera branche plus tard.</p>
      {error && <p className="mb-3 text-sm text-red-600">{error}</p>}
      <div className="mb-6 flex flex-wrap gap-2 text-sm">
        <span className="rounded bg-white px-3 py-1 shadow">SMS {badge(statut?.sms)}</span>
        <span className="rounded bg-white px-3 py-1 shadow">FCM {badge(statut?.fcm)}</span>
        <span className="rounded bg-white px-3 py-1 shadow">PSP {badge(statut?.psp)}</span>
      </div>
      <form className="mb-4 space-y-2 rounded-lg bg-white p-4 shadow" onSubmit={(e) => envoyer(e, "/api/v1/canaux/sms", { telephone, message })}>
        <h2 className="font-medium">SMS</h2>
        <input className="w-full rounded-md border px-3 py-2 text-sm" value={telephone} onChange={(e) => setTelephone(e.target.value)} />
        <input className="w-full rounded-md border px-3 py-2 text-sm" value={message} onChange={(e) => setMessage(e.target.value)} />
        <button className="rounded-md bg-primary px-3 py-1 text-sm text-white" disabled={statut?.sms === "OFF"}>Envoyer</button>
      </form>
      <form className="mb-4 space-y-2 rounded-lg bg-white p-4 shadow" onSubmit={(e) => envoyer(e, "/api/v1/canaux/push", { token, message })}>
        <h2 className="font-medium">Push FCM</h2>
        <input className="w-full rounded-md border px-3 py-2 text-sm" value={token} onChange={(e) => setToken(e.target.value)} />
        <button className="rounded-md bg-primary px-3 py-1 text-sm text-white" disabled={statut?.fcm === "OFF"}>Envoyer</button>
      </form>
      <form className="mb-4 space-y-2 rounded-lg bg-white p-4 shadow" onSubmit={(e) => envoyer(e, "/api/v1/canaux/psp", { fournisseur, message })}>
        <h2 className="font-medium">Paiement PSP</h2>
        <select className="w-full rounded-md border px-3 py-2 text-sm" value={fournisseur} onChange={(e) => setFournisseur(e.target.value)}>
          <option value="WAVE">Wave</option>
          <option value="ORANGE_MONEY">Orange Money</option>
          <option value="CARTE">Carte</option>
        </select>
        <button className="rounded-md bg-primary px-3 py-1 text-sm text-white" disabled={statut?.psp === "OFF"}>Simuler</button>
      </form>
      <section className="rounded-lg bg-white p-4 text-sm shadow">
        <h2 className="mb-2 font-medium">Journal mock</h2>
        {(statut?.journal ?? []).length === 0 && <p className="text-slate-500">Aucun envoi.</p>}
        <ul className="space-y-1">
          {(statut?.journal ?? []).map((j, i) => (
            <li key={i}>{j.canal} · {j.mode} · {j.cible} · {j.message}</li>
          ))}
        </ul>
      </section>
    </main>
  );
}
