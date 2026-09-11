import { FormEvent, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Annonce, Creneau, vitrineApi } from "../vitrine";

export function AnnonceDetailPage() {
  const { id } = useParams();
  const [annonce, setAnnonce] = useState<Annonce | null>(null);
  const [creneaux, setCreneaux] = useState<Creneau[]>([]);
  const [nom, setNom] = useState("");
  const [telephone, setTelephone] = useState("");
  const [debut, setDebut] = useState("");
  const [fin, setFin] = useState("");
  const [message, setMessage] = useState("");
  const [ok, setOk] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    vitrineApi.get(id).then(setAnnonce).catch((e) => setError(e.message));
    vitrineApi.dispo(id).then(setCreneaux).catch(() => undefined);
  }, [id]);

  async function reserver(e: FormEvent) {
    e.preventDefault();
    if (!id) return;
    try {
      await vitrineApi.reserver({ uniteId: id, nom, telephone, dateDebut: debut, dateFin: fin, message });
      setOk(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Reservation impossible");
    }
  }

  if (!annonce) return <p className="p-8">{error ?? "Chargement..."}</p>;
  return (
    <main className="mx-auto max-w-2xl p-8">
      <Link className="text-sm text-primary" to="/annonces">Annonces</Link>
      <h1 className="mt-2 text-2xl font-semibold text-primary">{annonce.libelle}</h1>
      <p className="text-sm text-slate-600">{annonce.designationBien} · {annonce.ville} · {annonce.loyer} {annonce.devise} / {annonce.periodicite}</p>
      <h2 className="mt-6 font-medium">Periodes reservees</h2>
      <ul className="mt-2 text-sm">
        {creneaux.map((c, i) => <li key={i}>{c.debut} → {c.fin} ({c.statut})</li>)}
        {creneaux.length === 0 && <li className="text-slate-500">Aucune reservation.</li>}
      </ul>
      {ok ? <p className="mt-6 text-emerald-700">Demande envoyee.</p> : (
        <form className="mt-6 space-y-3 rounded-lg bg-white p-4 shadow" onSubmit={reserver}>
          <h2 className="font-medium">Reserver</h2>
          <input className="w-full rounded-md border px-3 py-2" placeholder="Nom" value={nom} onChange={(e) => setNom(e.target.value)} required />
          <input className="w-full rounded-md border px-3 py-2" placeholder="Telephone" value={telephone} onChange={(e) => setTelephone(e.target.value)} />
          <input type="date" className="w-full rounded-md border px-3 py-2" value={debut} onChange={(e) => setDebut(e.target.value)} required />
          <input type="date" className="w-full rounded-md border px-3 py-2" value={fin} onChange={(e) => setFin(e.target.value)} required />
          <textarea className="w-full rounded-md border px-3 py-2" placeholder="Message" value={message} onChange={(e) => setMessage(e.target.value)} />
          {error && <p className="text-sm text-red-600">{error}</p>}
          <button className="rounded-md bg-primary px-4 py-2 text-white">Envoyer</button>
        </form>
      )}
    </main>
  );
}
