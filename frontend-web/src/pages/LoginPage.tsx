import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api, saveSession } from "../api";

export function LoginPage() {
  const navigate = useNavigate();
  const [identifiant, setIdentifiant] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setLoading(true); setError(null);
    try {
      saveSession(await api.login({ identifiant, motDePasse }));
      navigate("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Connexion impossible");
    } finally { setLoading(false); }
  }
  return (
    <main className="mx-auto mt-16 max-w-md rounded-lg bg-white p-8 shadow">
      <h1 className="mb-6 text-2xl font-semibold text-primary">Connexion</h1>
      <form className="space-y-4" onSubmit={onSubmit}>
        <label className="block text-sm">Email ou telephone
          <input className="mt-1 w-full rounded-md border px-3 py-2" value={identifiant} onChange={(e) => setIdentifiant(e.target.value)} required />
        </label>
        <label className="block text-sm">Mot de passe
          <input type="password" className="mt-1 w-full rounded-md border px-3 py-2" value={motDePasse} onChange={(e) => setMotDePasse(e.target.value)} required />
        </label>
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button type="submit" disabled={loading} className="w-full rounded-md bg-primary py-2 font-medium text-white">{loading ? "Connexion..." : "Se connecter"}</button>
      </form>
      <p className="mt-4 text-sm">Pas de compte ? <Link className="text-primary" to="/inscription">Creer un compte</Link></p>
    </main>
  );
}
