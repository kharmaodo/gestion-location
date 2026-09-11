import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api, clearSession, getAccessToken, MeResponse } from "../api";

type Dash = {
  biens: number;
  unites: number;
  unitesLibres: number;
  unitesOccupees: number;
  contratsActifs: number;
  echeancesAPayer: number;
  echeancesPartielles: number;
  echeancesPayees: number;
  encaisse: number;
  aRecouvrer: number;
  tauxOccupation: number;
};

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export function HomePage() {
  const navigate = useNavigate();
  const [me, setMe] = useState<MeResponse | null>(null);
  const [dash, setDash] = useState<Dash | null>(null);
  useEffect(() => {
    api.me().then(async (m) => {
      setMe(m);
      if (m.roles.includes("PROPRIETAIRE")) {
        const res = await fetch(`${API}/api/v1/dashboard`, {
          headers: { Authorization: `Bearer ${getAccessToken()}` },
        });
        if (res.ok) setDash(await res.json());
      }
    }).catch(() => { clearSession(); navigate("/connexion"); });
  }, [navigate]);
  async function logout() {
    try { await api.logout(); } finally { clearSession(); navigate("/connexion"); }
  }
  if (!me) return <p className="p-8">Chargement...</p>;
  const proprio = me.roles.includes("PROPRIETAIRE");
  return (
    <main className="mx-auto max-w-5xl p-8">
      <header className="mb-8 flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-primary">Espace {me.roles.join(", ")}</h1>
        <div className="flex flex-wrap gap-3 text-sm">
          <Link className="rounded-md border px-3 py-1" to="/annonces">Annonces</Link>
          {proprio && <Link className="rounded-md border px-3 py-1" to="/biens">Mes biens</Link>}
          {proprio && <Link className="rounded-md border px-3 py-1" to="/locataires">Locataires</Link>}
          {proprio && <Link className="rounded-md border px-3 py-1" to="/reservations">Reservations</Link>}
          {proprio && <Link className="rounded-md border px-3 py-1" to="/contrats">Contrats</Link>}
          {proprio && <Link className="rounded-md border px-3 py-1" to="/loyers">Loyers</Link>}
          <Link className="rounded-md border px-3 py-1" to="/messages">Messages</Link>
          <Link className="rounded-md border px-3 py-1" to="/securite">Securite</Link>
          <button className="rounded-md border px-3 py-1" onClick={logout}>Deconnexion</button>
        </div>
      </header>
      <p className="mb-4">Bonjour {me.prenom ?? me.email ?? "utilisateur"}.</p>
      {dash && (
        <section className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
          <Card label="Occupation" value={`${dash.tauxOccupation} %`} hint={`${dash.unitesOccupees}/${dash.unites} unites`} />
          <Card label="Encaisse" value={`${dash.encaisse} XOF`} hint={`${dash.echeancesPayees} quittances`} />
          <Card label="A recouvrer" value={`${dash.aRecouvrer} XOF`} hint={`${dash.echeancesAPayer + dash.echeancesPartielles} echeances`} />
          <Card label="Contrats actifs" value={`${dash.contratsActifs}`} hint={`${dash.biens} biens`} />
        </section>
      )}
    </main>
  );
}

function Card({ label, value, hint }: { label: string; value: string; hint: string }) {
  return (
    <div className="rounded-lg bg-white p-4 shadow">
      <p className="text-sm text-slate-500">{label}</p>
      <p className="text-xl font-semibold text-primary">{value}</p>
      <p className="text-xs text-slate-500">{hint}</p>
    </div>
  );
}
