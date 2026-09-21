import { FormEvent, useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { Annonce, vitrineApi } from "../vitrine";

export function AnnoncesPage() {
  const [params, setParams] = useSearchParams();
  const [ville, setVille] = useState(params.get("ville") ?? "");
  const [type, setType] = useState(params.get("type") ?? "");
  const [loyerMax, setLoyerMax] = useState(params.get("loyerMax") ?? "");
  const [items, setItems] = useState<Annonce[]>([]);
  const [error, setError] = useState<string | null>(null);
  const q = (params.get("q") ?? "").toLowerCase();

  async function load(e?: FormEvent) {
    e?.preventDefault();
    const next = new URLSearchParams();
    if (ville) next.set("ville", ville);
    if (type) next.set("type", type);
    if (loyerMax) next.set("loyerMax", loyerMax);
    if (q) next.set("q", q);
    setParams(next);
    try {
      setItems(await vitrineApi.list(ville || undefined));
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }
  useEffect(() => {
    vitrineApi.list(params.get("ville") || undefined).then(setItems).catch((e) => setError(e.message));
  }, [params]);

  const filtered = useMemo(
    () =>
      items.filter((a) => {
        if (type && a.type !== type) return false;
        if (loyerMax && Number(a.loyer) > Number(loyerMax)) return false;
        if (q) {
          const blob = `${a.libelle} ${a.designationBien} ${a.ville ?? ""} ${a.type}`.toLowerCase();
          if (!blob.includes(q)) return false;
        }
        return true;
      }),
    [items, type, loyerMax, q]
  );

  return (
    <main className="mx-auto max-w-5xl p-8">
      <h1 className="mb-4 text-2xl font-semibold text-primary">Annonces</h1>
      <form className="mb-6 grid gap-2 sm:grid-cols-4" onSubmit={load}>
        <input className="rounded-md border px-3 py-2" placeholder="Ville" value={ville} onChange={(e) => setVille(e.target.value)} />
        <select className="rounded-md border px-3 py-2" value={type} onChange={(e) => setType(e.target.value)}>
          <option value="">Tous types</option>
          <option value="CHAMBRE_SIMPLE">Chambre simple</option>
          <option value="CHAMBRE_SDB">Chambre + SDB</option>
          <option value="STUDIO">Studio</option>
          <option value="APPARTEMENT">Appartement</option>
          <option value="MAISON">Maison</option>
        </select>
        <input className="rounded-md border px-3 py-2" placeholder="Loyer max" value={loyerMax} onChange={(e) => setLoyerMax(e.target.value)} />
        <button className="rounded-md bg-primary px-4 py-2 text-white">Filtrer</button>
      </form>
      {(q || ville || type || loyerMax) && (
        <p className="mb-3 text-sm text-slate-500">
          {filtered.length} resultat(s){q ? ` · recherche « ${q} »` : ""}
        </p>
      )}
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="grid gap-3 md:grid-cols-2">
        {filtered.map((a) => (
          <Link key={a.uniteId} to={`/annonces/${a.uniteId}`} className="rounded-lg bg-white p-4 shadow hover:bg-slate-50">
            <p className="font-medium">{a.libelle} · {a.designationBien}</p>
            <p className="text-sm text-slate-600">{a.ville} · {a.type} · {a.loyer} {a.devise} / {a.periodicite}</p>
          </Link>
        ))}
      </div>
      {filtered.length === 0 && !error && (
        <p className="text-sm text-slate-500">
          Aucune annonce publiee. Pour apparaitre ici : Mes biens → unite → au moins 3 photos → Publier.
          Les unites occupees restent hors vitrine.
        </p>
      )}
    </main>
  );
}
