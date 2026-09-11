import { FormEvent, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { api } from "../api";

export function ResetPage() {
  const [params] = useSearchParams();
  const token = params.get("token") ?? "";
  const [pwd, setPwd] = useState("");
  const [done, setDone] = useState(false);
  const [error, setError] = useState<string | null>(null);
  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    try {
      await api.resetPassword(token, pwd);
      setDone(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Lien invalide");
    }
  }
  if (!token) return <p className="p-8">Lien invalide.</p>;
  return (
    <main className="mx-auto mt-16 max-w-md rounded-lg bg-white p-8 shadow">
      <h1 className="mb-4 text-2xl font-semibold text-primary">Nouveau mot de passe</h1>
      {done ? (
        <p className="text-sm">Mot de passe mis a jour. <Link className="text-primary" to="/connexion">Connexion</Link></p>
      ) : (
        <form className="space-y-4" onSubmit={onSubmit}>
          <label className="block text-sm">Mot de passe
            <input type="password" minLength={8} className="mt-1 w-full rounded-md border px-3 py-2" value={pwd} onChange={(e) => setPwd(e.target.value)} required />
          </label>
          {error && <p className="text-sm text-red-600">{error}</p>}
          <button className="w-full rounded-md bg-primary py-2 text-white">Enregistrer</button>
        </form>
      )}
    </main>
  );
}
