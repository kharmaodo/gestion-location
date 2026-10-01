import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api, saveSession } from "../api";
import { useI18n } from "../i18n";

export function RegisterPage() {
  const navigate = useNavigate();
  const { t, locale, setLocale } = useI18n();
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
      setError(err instanceof Error ? err.message : t("auth.register"));
    } finally { setLoading(false); }
  }
  return (
    <main className="mx-auto mt-16 max-w-md rounded-lg bg-white p-8 shadow">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-primary">{t("auth.create")}</h1>
        <select className="rounded-md border px-2 py-1 text-sm" value={locale} onChange={(e) => setLocale(e.target.value)}>
          <option value="fr">FR</option>
          <option value="en">EN</option>
          <option value="wo">WO</option>
        </select>
      </div>
      <form className="space-y-4" onSubmit={onSubmit}>
        <label className="flex gap-2 text-sm"><input type="radio" checked={typeCompte === "PROPRIETAIRE"} onChange={() => setTypeCompte("PROPRIETAIRE")} /> {t("auth.owner")}</label>
        <label className="flex gap-2 text-sm"><input type="radio" checked={typeCompte === "LOCATAIRE"} onChange={() => setTypeCompte("LOCATAIRE")} /> {t("auth.tenant")}</label>
        <label className="block text-sm">{t("auth.email")}<input type="email" className="mt-1 w-full rounded-md border px-3 py-2" value={email} onChange={(e) => setEmail(e.target.value)} required /></label>
        <label className="block text-sm">{t("auth.password")}<input type="password" minLength={8} className="mt-1 w-full rounded-md border px-3 py-2" value={motDePasse} onChange={(e) => setMotDePasse(e.target.value)} required /></label>
        <label className="flex gap-2 text-sm"><input type="checkbox" checked={consentementRgpd} onChange={(e) => setConsentementRgpd(e.target.checked)} required /> {t("auth.rgpd")}</label>
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button type="submit" disabled={loading} className="w-full rounded-md bg-primary py-2 font-medium text-white">{loading ? t("auth.creating") : t("auth.submitRegister")}</button>
      </form>
      <p className="mt-4 text-sm">{t("auth.haveAccount")} <Link className="text-primary" to="/connexion">{t("auth.submit")}</Link></p>
    </main>
  );
}
