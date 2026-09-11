import { FormEvent, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api";

export function SecurityPage() {
  const [secret, setSecret] = useState<string | null>(null);
  const [otpauth, setOtpauth] = useState<string | null>(null);
  const [code, setCode] = useState("");
  const [message, setMessage] = useState<string | null>(null);

  async function setup() {
    const res = await api.setup2fa();
    setSecret(res.secret);
    setOtpauth(res.otpauthUrl);
  }

  async function enable(e: FormEvent) {
    e.preventDefault();
    await api.enable2fa(code);
    setMessage("2FA activee");
  }

  return (
    <main className="mx-auto max-w-lg p-8">
      <Link className="text-sm text-primary" to="/">Retour</Link>
      <h1 className="my-4 text-2xl font-semibold text-primary">Securite du compte</h1>
      <button className="rounded-md border px-3 py-2 text-sm" onClick={setup}>Generer un secret TOTP</button>
      {secret && (
        <div className="mt-4 space-y-2 rounded-lg bg-white p-4 shadow text-sm">
          <p>Secret : <code>{secret}</code></p>
          <p className="break-all text-slate-600">{otpauth}</p>
          <p>Ajoutez-le dans Google Authenticator / Aegis puis validez un code.</p>
          <form className="flex gap-2" onSubmit={enable}>
            <input className="flex-1 rounded-md border px-3 py-2" value={code} onChange={(e) => setCode(e.target.value)} maxLength={6} />
            <button className="rounded-md bg-primary px-3 py-2 text-white">Activer</button>
          </form>
        </div>
      )}
      {message && <p className="mt-4 text-sm text-emerald-700">{message}</p>}
    </main>
  );
}
