import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { locatairesApi } from "../locataires";

export function NouveauDossierPage() {
  const navigate = useNavigate();
  const [nom, setNom] = useState("");
  const [prenom, setPrenom] = useState("");
  const [telephone, setTelephone] = useState("");
  const [email, setEmail] = useState("");
  const [pieceType, setPieceType] = useState("CNI");
  const [pieceNumero, setPieceNumero] = useState("");
  const [error, setError] = useState<string | null>(null);
  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    try {
      const d = await locatairesApi.create({ nom, prenom, telephone, email, pieceType, pieceNumero });
      navigate(`/locataires/${d.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }
  return (
    <main className="mx-auto max-w-lg p-8">
      <h1 className="mb-6 text-2xl font-semibold text-primary">Nouveau dossier</h1>
      <form className="space-y-3 rounded-lg bg-white p-6 shadow" onSubmit={onSubmit}>
        <input className="w-full rounded-md border px-3 py-2" placeholder="Nom" value={nom} onChange={(e) => setNom(e.target.value)} required />
        <input className="w-full rounded-md border px-3 py-2" placeholder="Prenom" value={prenom} onChange={(e) => setPrenom(e.target.value)} />
        <input className="w-full rounded-md border px-3 py-2" placeholder="Telephone" value={telephone} onChange={(e) => setTelephone(e.target.value)} />
        <input className="w-full rounded-md border px-3 py-2" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
        <select className="w-full rounded-md border px-3 py-2" value={pieceType} onChange={(e) => setPieceType(e.target.value)}>
          <option value="CNI">CNI</option>
          <option value="PASSEPORT">Passeport</option>
        </select>
        <input className="w-full rounded-md border px-3 py-2" placeholder="Numero piece" value={pieceNumero} onChange={(e) => setPieceNumero(e.target.value)} />
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button className="w-full rounded-md bg-primary py-2 text-white">Enregistrer</button>
      </form>
      <p className="mt-4 text-sm"><Link className="text-primary" to="/locataires">Retour</Link></p>
    </main>
  );
}
