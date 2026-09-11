import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api, saveSession } from "../api";

export function RegisterPage() {
  const navigate = useNavigate();
  const [typeCompte, setTypeCompte] = useState("PROPRIETAIRE");
  const [email, setEmail] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [consentementRgpd, setConsentementRgpd] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setLoading(true); setError(null);
    try {
      saveSession(await api.register({ typeCompte, email, motDePasse, consentementRgpd }));
      navigate("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Inscription impossible");
    } finally { setLoading(false); }
  }
  return (
    <main className="mx-auto mt-16 max-w-md rounded-lg bg-white p-8 shadow">
      <h1 className="mb-6 text-2xl font-semibold text-primary">Creer un compte</h1>
      <form className="space-y-4" onSubmit={onSubmit}>
        <label className="flex gap-2 text-sm"><input type="radio" checked={typeCompte === "PROPRIETAIRE"} onChange={() => setTypeCompte("PROPRIETAIRE")} /> Proprietaire</label>
        <label className="flex gap-2 text-sm"><input type="radio" checked={typeCompte === "LOCATAIRE"} onChange={() => setTypeCompte("LOCATAIRE")} /> Locataire / prospect</label>
        <label className="block text-sm">Email<input type="email" className="mt-1 w-full rounded-md border px-3 py-2" value={email} onChange={(e) => setEmail(e.target.value)} required /></label>
        <label className="block text-sm">Mot de passe<input type="password" minLength={8} className="mt-1 w-full rounded-md border px-3 py-2" value={motDePasse} onChange={(e) => setMotDePasse(e.target.value)} required /></label>
        <label className="flex gap-2 text-sm"><input type="checkbox" checked={consentementRgpd} onChange={(e) => setConsentementRgpd(e.target.checked)} required /> J accepte le traitement de mes donnees</label>
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button type="submit" disabled={loading} className="w-full rounded-md bg-primary py-2 font-medium text-white">{loading ? "Creation..." : "Creer mon compte"}</button>
      </form>
      <p className="mt-4 text-sm">Deja un compte ? <Link className="text-primary" to="/connexion">Se connecter</Link></p>
    </main>
  );
}
