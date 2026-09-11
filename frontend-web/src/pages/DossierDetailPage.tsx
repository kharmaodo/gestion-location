import { FormEvent, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Dossier, locatairesApi } from "../locataires";

export function DossierDetailPage() {
  const { id } = useParams();
  const [dossier, setDossier] = useState<Dossier | null>(null);
  const [type, setType] = useState("CNI");
  const [file, setFile] = useState<File | null>(null);
  const [commentaire, setCommentaire] = useState("");
  const [error, setError] = useState<string | null>(null);

  function reload() {
    if (id) locatairesApi.get(id).then(setDossier).catch((e) => setError(e.message));
  }
  useEffect(reload, [id]);

  async function upload(e: FormEvent) {
    e.preventDefault();
    if (!id || !file) return;
    try {
      await locatairesApi.upload(id, type, file);
      setFile(null);
      reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Upload impossible");
    }
  }

  async function decide(statut: string) {
    if (!id) return;
    try {
      await locatairesApi.kyc(id, statut, commentaire);
      reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Decision impossible");
    }
  }

  if (!dossier) return <p className="p-8">{error ?? "Chargement..."}</p>;
  return (
    <main className="mx-auto max-w-3xl p-8">
      <Link className="text-sm text-primary" to="/locataires">Locataires</Link>
      <h1 className="mt-2 text-2xl font-semibold text-primary">{dossier.prenom} {dossier.nom}</h1>
      <p className="text-sm text-slate-600">{dossier.telephone} · {dossier.email} · KYC {dossier.kycStatut}</p>
      {error && <p className="mt-2 text-sm text-red-600">{error}</p>}
      <ul className="mt-6 space-y-2">
        {(dossier.documents ?? []).map((d) => (
          <li key={d.id} className="rounded-lg bg-white p-3 text-sm shadow">{d.type} — {d.nomFichier} ({d.kycStatut})</li>
        ))}
      </ul>
      <form className="mt-6 space-y-3 rounded-lg bg-white p-4 shadow" onSubmit={upload}>
        <h2 className="font-medium">Ajouter un document</h2>
        <select className="w-full rounded-md border px-3 py-2 text-sm" value={type} onChange={(e) => setType(e.target.value)}>
          <option value="CNI">CNI</option>
          <option value="PASSEPORT">Passeport</option>
          <option value="JUSTIFICATIF_DOMICILE">Justificatif</option>
          <option value="PHOTO">Photo</option>
          <option value="AUTRE">Autre</option>
        </select>
        <input type="file" onChange={(e) => setFile(e.target.files?.[0] ?? null)} />
        <button className="rounded-md bg-primary px-4 py-2 text-sm text-white">Telecharger</button>
      </form>
      <div className="mt-6 space-y-2 rounded-lg bg-white p-4 shadow">
        <h2 className="font-medium">Decision KYC</h2>
        <textarea className="w-full rounded-md border px-3 py-2 text-sm" placeholder="Commentaire" value={commentaire} onChange={(e) => setCommentaire(e.target.value)} />
        <div className="flex gap-2">
          <button className="rounded-md bg-emerald-700 px-3 py-2 text-sm text-white" onClick={() => decide("VALIDE")}>Valider</button>
          <button className="rounded-md bg-red-700 px-3 py-2 text-sm text-white" onClick={() => decide("REJETE")}>Rejeter</button>
        </div>
      </div>
    </main>
  );
}
