import { useEffect, useState } from "react";
import { getAccessToken } from "../api";
import { api } from "../api";

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
  const [dash, setDash] = useState<Dash | null>(null);
  const [roles, setRoles] = useState<string[]>([]);
  useEffect(() => {
    api.me().then(async (m) => {
      setRoles(m.roles);
      if (m.roles.includes("PROPRIETAIRE")) {
        const res = await fetch(`${API}/api/v1/dashboard`, {
          headers: { Authorization: `Bearer ${getAccessToken()}` },
        });
        if (res.ok) setDash(await res.json());
      }
    }).catch(() => undefined);
  }, []);
  return (
    <main className="mx-auto max-w-5xl p-8">
      <h1 className="mb-6 text-2xl font-semibold text-primary">Tableau de bord</h1>
      {dash ? (
        <section className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
          <Card label="Occupation" value={`${dash.tauxOccupation} %`} hint={`${dash.unitesOccupees}/${dash.unites} unites`} />
          <Card label="Encaisse" value={`${dash.encaisse} XOF`} hint={`${dash.echeancesPayees} quittances`} />
          <Card label="A recouvrer" value={`${dash.aRecouvrer} XOF`} hint={`${dash.echeancesAPayer + dash.echeancesPartielles} echeances`} />
          <Card label="Contrats actifs" value={`${dash.contratsActifs}`} hint={`${dash.biens} biens`} />
        </section>
      ) : (
        <p className="text-sm text-slate-600">
          {roles.includes("LOCATAIRE") ? "Bienvenue dans votre espace locataire." : "Chargement des indicateurs..."}
        </p>
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
