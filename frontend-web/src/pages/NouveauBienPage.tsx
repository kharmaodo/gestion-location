import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { biensApi } from "../biens";

export function NouveauBienPage() {
  const navigate = useNavigate();
  const [designation, setDesignation] = useState("");
  const [type, setType] = useState("MAISON");
  const [ville, setVille] = useState("");
  const [adresse, setAdresse] = useState("");
  const [error, setError] = useState<string | null>(null);
  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    try {
      const bien = await biensApi.create({ designation, type, ville, adresse });
      navigate(`/biens/${bien.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }
  return (
    <main className="mx-auto max-w-lg p-8">
      <h1 className="mb-6 text-2xl font-semibold text-primary">Nouveau bien</h1>
      <form className="space-y-4 rounded-lg bg-white p-6 shadow" onSubmit={onSubmit}>
        <label className="block text-sm">Designation
          <input className="mt-1 w-full rounded-md border px-3 py-2" value={designation} onChange={(e) => setDesignation(e.target.value)} required />
        </label>
        <label className="block text-sm">Type
          <select className="mt-1 w-full rounded-md border px-3 py-2" value={type} onChange={(e) => setType(e.target.value)}>
            <option value="MAISON">Maison</option>
            <option value="IMMEUBLE">Immeuble</option>
            <option value="RESIDENCE">Residence</option>
          </select>
        </label>
        <label className="block text-sm">Ville
          <input className="mt-1 w-full rounded-md border px-3 py-2" value={ville} onChange={(e) => setVille(e.target.value)} />
        </label>
        <label className="block text-sm">Adresse
          <input className="mt-1 w-full rounded-md border px-3 py-2" value={adresse} onChange={(e) => setAdresse(e.target.value)} />
        </label>
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button className="w-full rounded-md bg-primary py-2 text-white">Enregistrer</button>
      </form>
      <p className="mt-4 text-sm"><Link className="text-primary" to="/biens">Retour</Link></p>
    </main>
  );
}
