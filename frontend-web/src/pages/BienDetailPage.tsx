import { FormEvent, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Bien, biensApi } from "../biens";

export function BienDetailPage() {
  const { id } = useParams();
  const [bien, setBien] = useState<Bien | null>(null);
  const [libelle, setLibelle] = useState("Chambre 1");
  const [type, setType] = useState("CHAMBRE_SDB");
  const [loyer, setLoyer] = useState("85000");
  const [meuble, setMeuble] = useState(true);
  const [periodicite, setPeriodicite] = useState("MENSUEL");
  const [error, setError] = useState<string | null>(null);

  function reload() {
    if (id) biensApi.get(id).then(setBien).catch((e) => setError(e.message));
  }
  useEffect(reload, [id]);

  async function addUnite(e: FormEvent) {
    e.preventDefault();
    if (!id) return;
    try {
      await biensApi.addUnite(id, { libelle, type, loyer: Number(loyer), meuble, periodicite, jourEcheance: 5 });
      reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }

  async function changePer(uniteId: string, value: string) {
    if (!id) return;
    try {
      await biensApi.setPeriodicite(id, uniteId, { periodicite: value, jourEcheance: 5 });
      reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }

  if (!bien) return <p className="p-8">{error ?? "Chargement..."}</p>;
  return (
    <main className="mx-auto max-w-3xl p-8">
      <Link className="text-sm text-primary" to="/biens">Mes biens</Link>
      <h1 className="mt-2 text-2xl font-semibold text-primary">{bien.designation}</h1>
      <p className="text-sm text-slate-600">{bien.type} · {bien.ville} · {bien.adresse}</p>
      {error && <p className="mt-2 text-sm text-red-600">{error}</p>}
      <section className="mt-6 space-y-3">
        {(bien.unitesDetail ?? []).map((u) => (
          <div key={u.id} className="rounded-lg bg-white p-4 shadow">
            <div className="flex justify-between">
              <div>
                <p className="font-medium">{u.libelle}</p>
                <p className="text-sm text-slate-600">{u.type} · {u.loyer} {u.devise} · {u.meuble ? "meuble" : "non meuble"}</p>
              </div>
              <span className="text-sm">{u.statut}</span>
            </div>
            <label className="mt-2 block text-sm">Periodicite
              <select className="ml-2 rounded-md border px-2 py-1" value={u.periodicite} onChange={(e) => changePer(u.id, e.target.value)}>
                <option value="JOURNALIER">Journalier</option>
                <option value="HEBDOMADAIRE">Hebdomadaire</option>
                <option value="MENSUEL">Mensuel</option>
              </select>
            </label>
          </div>
        ))}
      </section>
      <form className="mt-8 space-y-3 rounded-lg bg-white p-4 shadow" onSubmit={addUnite}>
        <h2 className="font-medium">Ajouter une unite</h2>
        <input className="w-full rounded-md border px-3 py-2 text-sm" value={libelle} onChange={(e) => setLibelle(e.target.value)} />
        <select className="w-full rounded-md border px-3 py-2 text-sm" value={type} onChange={(e) => setType(e.target.value)}>
          <option value="CHAMBRE_SIMPLE">Chambre simple</option>
          <option value="CHAMBRE_SDB">Chambre + SDB</option>
          <option value="STUDIO">Studio</option>
          <option value="APPARTEMENT">Appartement</option>
          <option value="MAISON">Maison</option>
        </select>
        <input className="w-full rounded-md border px-3 py-2 text-sm" value={loyer} onChange={(e) => setLoyer(e.target.value)} />
        <label className="flex gap-2 text-sm"><input type="checkbox" checked={meuble} onChange={(e) => setMeuble(e.target.checked)} /> Meuble</label>
        <select className="w-full rounded-md border px-3 py-2 text-sm" value={periodicite} onChange={(e) => setPeriodicite(e.target.value)}>
          <option value="MENSUEL">Mensuel</option>
          <option value="HEBDOMADAIRE">Hebdomadaire</option>
          <option value="JOURNALIER">Journalier</option>
        </select>
        <button className="rounded-md bg-primary px-4 py-2 text-sm text-white">Ajouter</button>
      </form>
    </main>
  );
}
