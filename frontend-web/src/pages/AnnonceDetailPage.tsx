import { FormEvent, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getAccessToken } from "../api";
import { Annonce, Avis, Creneau, vitrineApi } from "../vitrine";

export function AnnonceDetailPage() {
  const { id } = useParams();
  const [annonce, setAnnonce] = useState<Annonce | null>(null);
  const [creneaux, setCreneaux] = useState<Creneau[]>([]);
  const [avis, setAvis] = useState<Avis[]>([]);
  const [nom, setNom] = useState("");
  const [telephone, setTelephone] = useState("");
  const [debut, setDebut] = useState("");
  const [fin, setFin] = useState("");
  const [message, setMessage] = useState("");
  const [note, setNote] = useState("5");
  const [commentaire, setCommentaire] = useState("");
  const [ok, setOk] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function loadAvis() {
    if (id) vitrineApi.avis(id).then(setAvis).catch(() => undefined);
  }

  useEffect(() => {
    if (!id) return;
    vitrineApi.get(id).then(setAnnonce).catch((e) => setError(e.message));
    vitrineApi.dispo(id).then(setCreneaux).catch(() => undefined);
    loadAvis();
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

  async function publier(e: FormEvent) {
    e.preventDefault();
    if (!id) return;
    try {
      await vitrineApi.publierAvis({ cibleUniteId: id, note: Number(note), commentaire });
      setCommentaire("");
      loadAvis();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Avis impossible");
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
      <section className="mt-6 rounded-lg bg-white p-4 shadow">
        <h2 className="font-medium">Avis</h2>
        <ul className="mt-2 space-y-2 text-sm">
          {avis.map((a) => (
            <li key={a.id} className="border-t pt-2">
              <span className="font-medium">{a.note}/5</span> — {a.commentaire || "—"}
            </li>
          ))}
          {avis.length === 0 && <li className="text-slate-500">Aucun avis.</li>}
        </ul>
        {getAccessToken() ? (
          <form className="mt-3 space-y-2" onSubmit={publier}>
            <select className="w-full rounded-md border px-3 py-2 text-sm" value={note} onChange={(e) => setNote(e.target.value)}>
              {[1, 2, 3, 4, 5].map((n) => <option key={n} value={n}>{n} / 5</option>)}
            </select>
            <textarea className="w-full rounded-md border px-3 py-2 text-sm" placeholder="Commentaire" value={commentaire} onChange={(e) => setCommentaire(e.target.value)} />
            <button className="rounded-md bg-primary px-3 py-1 text-sm text-white">Publier un avis</button>
          </form>
        ) : (
          <p className="mt-2 text-xs text-slate-500">Connectez-vous pour laisser un avis.</p>
        )}
      </section>
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
