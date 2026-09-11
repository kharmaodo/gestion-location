import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api, saveSession } from "../api";

export function LoginPage() {
  const navigate = useNavigate();
  const [identifiant, setIdentifiant] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [otp, setOtp] = useState("");
  const [pending, setPending] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setLoading(true); setError(null);
    try {
      if (pending) {
        saveSession(await api.verify2fa(pending, otp));
        navigate("/");
        return;
      }
      const tokens = await api.login({ identifiant, motDePasse, otp: otp || undefined });
      if (tokens.requiresTwoFactor && tokens.pendingToken) {
        setPending(tokens.pendingToken);
        return;
      }
      saveSession(tokens);
      navigate("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Connexion impossible");
    } finally { setLoading(false); }
  }

  return (
    <main className="mx-auto mt-16 max-w-md rounded-lg bg-white p-8 shadow">
      <h1 className="mb-6 text-2xl font-semibold text-primary">{pending ? "Code 2FA" : "Connexion"}</h1>
      <form className="space-y-4" onSubmit={onSubmit}>
        {!pending && (
          <>
            <label className="block text-sm">Email ou telephone
              <input className="mt-1 w-full rounded-md border px-3 py-2" value={identifiant} onChange={(e) => setIdentifiant(e.target.value)} required />
            </label>
            <label className="block text-sm">Mot de passe
              <input type="password" className="mt-1 w-full rounded-md border px-3 py-2" value={motDePasse} onChange={(e) => setMotDePasse(e.target.value)} required />
            </label>
          </>
        )}
        {pending && (
          <label className="block text-sm">Code authenticator
            <input className="mt-1 w-full rounded-md border px-3 py-2 tracking-widest" value={otp} onChange={(e) => setOtp(e.target.value)} required maxLength={6} />
          </label>
        )}
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button type="submit" disabled={loading} className="w-full rounded-md bg-primary py-2 font-medium text-white">{loading ? "..." : pending ? "Valider" : "Se connecter"}</button>
      </form>
      <p className="mt-4 text-sm"><Link className="text-primary" to="/mot-de-passe-oublie">Mot de passe oublie</Link></p>
      <p className="mt-2 text-sm">Pas de compte ? <Link className="text-primary" to="/inscription">Creer un compte</Link></p>
    </main>
  );
}
