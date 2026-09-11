import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api, clearSession, MeResponse } from "../api";

export function HomePage() {
  const navigate = useNavigate();
  const [me, setMe] = useState<MeResponse | null>(null);
  useEffect(() => {
    api.me().then(setMe).catch(() => { clearSession(); navigate("/connexion"); });
  }, [navigate]);
  async function logout() {
    try { await api.logout(); } finally { clearSession(); navigate("/connexion"); }
  }
  if (!me) return <p className="p-8">Chargement...</p>;
  return (
    <main className="mx-auto max-w-3xl p-8">
      <header className="mb-8 flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-primary">Espace {me.roles.join(", ")}</h1>
        <div className="flex gap-3 text-sm">
          <Link className="rounded-md border px-3 py-1" to="/securite">Securite</Link>
          <button className="rounded-md border px-3 py-1" onClick={logout}>Deconnexion</button>
        </div>
      </header>
      <section className="rounded-lg bg-white p-6 shadow">
        <p>Bonjour {me.prenom ?? me.email ?? "utilisateur"}.</p>
      </section>
    </main>
  );
}
