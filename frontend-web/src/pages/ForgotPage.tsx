import { FormEvent, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api";

export function ForgotPage() {
  const [identifiant, setIdentifiant] = useState("");
  const [done, setDone] = useState(false);
  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    await api.forgotPassword(identifiant);
    setDone(true);
  }
  return (
    <main className="mx-auto mt-16 max-w-md rounded-lg bg-white p-8 shadow">
      <h1 className="mb-4 text-2xl font-semibold text-primary">Mot de passe oublie</h1>
      {done ? (
        <p className="text-sm">Si un compte existe, un lien a ete envoye (voir MailHog http://localhost:8025).</p>
      ) : (
        <form className="space-y-4" onSubmit={onSubmit}>
          <label className="block text-sm">Email ou telephone
            <input className="mt-1 w-full rounded-md border px-3 py-2" value={identifiant} onChange={(e) => setIdentifiant(e.target.value)} required />
          </label>
          <button className="w-full rounded-md bg-primary py-2 text-white">Envoyer le lien</button>
        </form>
      )}
      <p className="mt-4 text-sm"><Link className="text-primary" to="/connexion">Retour</Link></p>
    </main>
  );
}
