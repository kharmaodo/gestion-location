import { useEffect, useMemo, useState } from "react";
import { Link, useLocation, useSearchParams } from "react-router-dom";
import { Dossier, locatairesApi } from "../locataires";

function csvCell(v?: string) {
  const s = v ?? "";
  if (/[",\n]/.test(s)) return `"${s.replaceAll('"', '""')}"`;
  return s;
}

export function LocatairesPage() {
  const [params] = useSearchParams();
  const location = useLocation();
  const flash = (location.state as { kycStatut?: string } | null)?.kycStatut;
  const q = (params.get("q") ?? "").toLowerCase();
  const [items, setItems] = useState<Dossier[]>([]);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    locatairesApi.list().then(setItems).catch((e) => setError(e.message));
  }, []);
  const filtered = useMemo(
    () =>
      items.filter(
        (d) =>
          !q ||
          `${d.prenom ?? ""} ${d.nom} ${d.telephone ?? ""} ${d.email ?? ""}`.toLowerCase().includes(q)
      ),
    [items, q]
  );

  function exporter() {
    const header = "prenom,nom,telephone,email,kyc\n";
    const rows = filtered.map((d) =>
      [d.prenom, d.nom, d.telephone, d.email, d.kycStatut].map(csvCell).join(",")
    ).join("\n");
    const blob = new Blob([header + rows], { type: "text/csv;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "locataires.csv";
    a.click();
    URL.revokeObjectURL(url);
  }

  return (
    <main className="mx-auto max-w-5xl p-8">
      <div className="mb-6 flex items-center justify-between gap-2">
        <h1 className="text-2xl font-semibold text-primary">Locataires</h1>
        <div className="flex gap-2">
          <button className="rounded-md border px-4 py-2 text-sm" type="button" onClick={exporter} disabled={filtered.length === 0}>
            Export CSV
          </button>
          <Link className="rounded-md bg-primary px-4 py-2 text-sm text-white" to="/locataires/nouveau">+ Dossier</Link>
        </div>
      </div>
      {flash && <p className="mb-3 rounded-md bg-emerald-50 px-3 py-2 text-sm">KYC mis a jour : {flash}</p>}
      {q && <p className="mb-3 text-sm text-slate-500">Filtre : {q}</p>}
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="space-y-3">
        {filtered.map((d) => (
          <Link key={d.id} to={`/locataires/${d.id}`} className="block rounded-lg bg-white p-4 shadow hover:bg-slate-50">
            <div className="flex justify-between">
              <div>
                <p className="font-medium">{d.prenom} {d.nom}</p>
                <p className="text-sm text-slate-600">{d.telephone ?? d.email ?? "—"}</p>
              </div>
              <span className="text-sm">{d.kycStatut}</span>
            </div>
          </Link>
        ))}
        {filtered.length === 0 && !error && <p className="text-sm text-slate-500">Aucun dossier.</p>}
      </div>
    </main>
  );
}
