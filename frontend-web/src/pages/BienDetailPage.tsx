import { FormEvent, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Bien, Media, biensApi } from "../biens";

export function BienDetailPage() {
  const { id } = useParams();
  const [bien, setBien] = useState<Bien | null>(null);
  const [libelle, setLibelle] = useState("Chambre 1");
  const [type, setType] = useState("CHAMBRE_SDB");
  const [loyer, setLoyer] = useState("85000");
  const [meuble, setMeuble] = useState(true);
  const [periodicite, setPeriodicite] = useState("MENSUEL");
  const [error, setError] = useState<string | null>(null);
  const [medias, setMedias] = useState<Record<string, Media[]>>({});
  const [photoUrl, setPhotoUrl] = useState<Record<string, string>>({});

  function reload() {
    if (id) biensApi.get(id).then(async (b) => {
      setBien(b);
      const next: Record<string, Media[]> = {};
      for (const u of b.unitesDetail ?? []) {
        next[u.id] = await biensApi.medias(u.id).catch(() => []);
      }
      setMedias(next);
    }).catch((e) => setError(e.message));
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

  async function togglePub(uniteId: string, publie: boolean) {
    if (!id) return;
    try {
      await biensApi.publier(id, uniteId, publie);
      reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }

  async function addPhoto(uniteId: string) {
    const url = (photoUrl[uniteId] ?? "").trim();
    if (!url) return;
    try {
      await biensApi.addMedia(uniteId, url);
      setPhotoUrl((s) => ({ ...s, [uniteId]: "" }));
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
              <span className="text-sm">{u.statut} {u.publie ? "· publie" : "· brouillon"}</span>
            </div>
            <div className="mt-3 flex flex-wrap gap-2">
              {(medias[u.id] ?? []).map((m) => (
                <img key={m.id} src={m.url} alt="" className="h-20 w-20 rounded object-cover bg-slate-100" />
              ))}
              <span className="self-center text-xs text-slate-500">{(medias[u.id] ?? []).length}/3 photos min</span>
            </div>
            <div className="mt-2 flex gap-2">
              <input className="flex-1 rounded-md border px-2 py-1 text-sm" placeholder="URL photo"
                value={photoUrl[u.id] ?? ""} onChange={(e) => setPhotoUrl((s) => ({ ...s, [u.id]: e.target.value }))} />
              <button type="button" className="rounded-md border px-3 py-1 text-sm" onClick={() => addPhoto(u.id)}>Ajouter photo</button>
            </div>
            <label className="mt-2 block text-sm">Periodicite
              <select className="ml-2 rounded-md border px-2 py-1" value={u.periodicite} onChange={(e) => changePer(u.id, e.target.value)}>
                <option value="JOURNALIER">Journalier</option>
                <option value="HEBDOMADAIRE">Hebdomadaire</option>
                <option value="MENSUEL">Mensuel</option>
              </select>
            </label>
            <button className="mt-2 rounded-md border px-3 py-1 text-sm" onClick={() => togglePub(u.id, !u.publie)}>
              {u.publie ? "Depublier" : "Publier"}
            </button>
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
