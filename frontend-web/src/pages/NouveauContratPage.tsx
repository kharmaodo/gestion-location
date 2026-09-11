import { FormEvent, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Bien, biensApi } from "../biens";
import { contratsApi } from "../contrats";
import { Dossier, locatairesApi } from "../locataires";

export function NouveauContratPage() {
  const navigate = useNavigate();
  const [biens, setBiens] = useState<Bien[]>([]);
  const [dossiers, setDossiers] = useState<Dossier[]>([]);
  const [uniteId, setUniteId] = useState("");
  const [dossierId, setDossierId] = useState("");
  const [dateDebut, setDateDebut] = useState("");
  const [loyer, setLoyer] = useState("");
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all(biens.map(() => null));
    biensApi.list().then(async (list) => {
      const details = await Promise.all(list.map((b) => biensApi.get(b.id)));
      setBiens(details);
    }).catch((e) => setError(e.message));
    locatairesApi.list().then(setDossiers).catch(() => undefined);
  }, []);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    try {
      const c = await contratsApi.create({
        uniteId,
        dossierId: dossierId || null,
        dateDebut,
        loyer: loyer ? Number(loyer) : null,
      });
      navigate(`/contrats/${c.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }

  return (
    <main className="mx-auto max-w-lg p-8">
      <h1 className="mb-6 text-2xl font-semibold text-primary">Nouveau contrat</h1>
      <form className="space-y-3 rounded-lg bg-white p-6 shadow" onSubmit={onSubmit}>
        <select className="w-full rounded-md border px-3 py-2" value={uniteId} onChange={(e) => setUniteId(e.target.value)} required>
          <option value="">Unite</option>
          {biens.flatMap((b) => (b.unitesDetail ?? []).map((u) => (
            <option key={u.id} value={u.id}>{b.designation} — {u.libelle} ({u.statut})</option>
          )))}
        </select>
        <select className="w-full rounded-md border px-3 py-2" value={dossierId} onChange={(e) => setDossierId(e.target.value)}>
          <option value="">Dossier locataire (optionnel)</option>
          {dossiers.map((d) => <option key={d.id} value={d.id}>{d.prenom} {d.nom} · {d.kycStatut}</option>)}
        </select>
        <input type="date" className="w-full rounded-md border px-3 py-2" value={dateDebut} onChange={(e) => setDateDebut(e.target.value)} required />
        <input className="w-full rounded-md border px-3 py-2" placeholder="Loyer (vide = loyer unite)" value={loyer} onChange={(e) => setLoyer(e.target.value)} />
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button className="w-full rounded-md bg-primary py-2 text-white">Creer en brouillon</button>
      </form>
      <p className="mt-4 text-sm"><Link className="text-primary" to="/contrats">Retour</Link></p>
    </main>
  );
}
