import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api, getAccessToken } from "../api";

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

type Alerte = {
  type: string;
  niveau: string;
  echeanceId: string;
  contratId: string;
  periodeFin: string;
  statutEcheance: string;
  montant: number;
  message: string;
};

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export function HomePage() {
  const [dash, setDash] = useState<Dash | null>(null);
  const [alertes, setAlertes] = useState<Alerte[]>([]);
  const [roles, setRoles] = useState<string[]>([]);
  useEffect(() => {
    api.me().then(async (m) => {
      setRoles(m.roles);
      if (m.roles.includes("PROPRIETAIRE")) {
        const headers = { Authorization: `Bearer ${getAccessToken()}` };
        const [d, a] = await Promise.all([
          fetch(`${API}/api/v1/dashboard`, { headers }),
          fetch(`${API}/api/v1/dashboard/alertes`, { headers }),
        ]);
        if (d.ok) setDash(await d.json());
        if (a.ok) setAlertes(await a.json());
      }
    }).catch(() => undefined);
  }, []);
  const critiques = alertes.filter((x) => x.niveau === "CRITIQUE");
  return (
    <main className="mx-auto max-w-5xl p-8">
      <h1 className="mb-6 text-2xl font-semibold text-primary">Tableau de bord</h1>
      {dash ? (
        <section className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
          <Card label="Occupation" value={`${dash.tauxOccupation} %`} hint={`${dash.unitesOccupees}/${dash.unites} unites`} />
          <Card label="Encaisse" value={`${dash.encaisse} XOF`} hint={`${dash.echeancesPayees} quittances`} />
          <Card label="A recouvrer" value={`${dash.aRecouvrer} XOF`} hint={`${dash.echeancesAPayer + dash.echeancesPartielles} echeances`} />
          <Card label="Alertes" value={`${critiques.length}`} hint={`${alertes.length} signaux`} />
        </section>
      ) : (
        <p className="text-sm text-slate-600">
          {roles.includes("LOCATAIRE") ? "Bienvenue dans votre espace locataire." : "Chargement des indicateurs..."}
        </p>
      )}
      {alertes.length > 0 && (
        <section className="mt-8 space-y-2">
          <h2 className="text-lg font-medium">Alertes loyers</h2>
          {alertes.map((al) => (
            <Link
              key={al.echeanceId}
              to="/loyers"
              className={`block rounded-lg p-3 text-sm shadow ${al.niveau === "CRITIQUE" ? "bg-red-50" : "bg-white"}`}
            >
              <span className="mr-2 rounded px-1.5 py-0.5 text-xs font-medium {al.niveau === "CRITIQUE" ? "bg-red-100" : "bg-slate-100"}">
                {al.niveau}
              </span>
              {al.message} · {al.montant} · {al.statutEcheance}
            </Link>
          ))}
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
