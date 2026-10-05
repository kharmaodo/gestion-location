import { FormEvent, useEffect, useState } from "react";
import { Link, NavLink, Outlet, useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { api, clearSession, MeResponse } from "../api";
import { useI18n } from "../i18n";
import { linksFor } from "../nav";
import { notificationsApi } from "../notifications";

export function AppShell() {
  const navigate = useNavigate();
  const location = useLocation();
  const [params] = useSearchParams();
  const { t, locale, setLocale } = useI18n();
  const [me, setMe] = useState<MeResponse | null>(null);
  const [q, setQ] = useState(params.get("q") ?? "");
  const [open, setOpen] = useState(false);
  const [nonLues, setNonLues] = useState(0);

  useEffect(() => {
    api.me().then(setMe).catch(() => {
      clearSession();
      navigate("/connexion");
    });
    notificationsApi.list().then((list) => setNonLues(list.filter((n) => !n.lu).length)).catch(() => undefined);
  }, [navigate, location.pathname]);

  const links = linksFor(me?.roles ?? []);
  const proprio = me?.roles.includes("PROPRIETAIRE") ?? false;
  const nom = [me?.prenom, me?.nom].filter(Boolean).join(" ") || me?.email || "\u2026";

  function onSearch(e: FormEvent) {
    e.preventDefault();
    const query = q.trim();
    const target = location.pathname.startsWith("/annonces")
      ? "/annonces"
      : location.pathname.startsWith("/contrats")
        ? "/contrats"
        : proprio
          ? location.pathname.startsWith("/locataires")
            ? "/locataires"
            : "/biens"
          : "/annonces";
    navigate(query ? `${target}?q=${encodeURIComponent(query)}` : target);
  }

  async function logout() {
    try {
      await api.logout();
    } finally {
      clearSession();
      navigate("/connexion");
    }
  }

  const itemClass = ({ isActive }: { isActive: boolean }) =>
    `block rounded-md px-3 py-2 text-sm ${isActive ? "bg-primary text-white" : "text-slate-700 hover:bg-slate-100"}`;

  return (
    <div className="flex min-h-screen flex-col bg-slate-50">
      <header className="sticky top-0 z-20 flex items-center gap-3 border-b bg-white px-4 py-3">
        <button className="rounded-md border px-2 py-1 text-sm lg:hidden" onClick={() => setOpen((o) => !o)}>
          {t("nav.menu")}
        </button>
        <Link to="/" className="font-semibold text-primary">{t("app.name")}</Link>
        <form className="mx-auto hidden max-w-xl flex-1 md:block" onSubmit={onSearch}>
          <input
            className="w-full rounded-md border px-3 py-1.5 text-sm"
            placeholder={t("nav.search")}
            value={q}
            onChange={(e) => setQ(e.target.value)}
          />
        </form>
        <Link to="/notifications" className="rounded-md border px-2 py-1 text-sm">
          {t("nav.notifications")}{nonLues > 0 ? ` (${nonLues})` : ""}
        </Link>
        <select className="rounded-md border px-2 py-1 text-sm" value={locale} onChange={(e) => setLocale(e.target.value)}>
          <option value="fr">FR</option>
          <option value="en">EN</option>
          <option value="wo">WO</option>
        </select>
        <span className="hidden text-sm font-medium sm:inline">{nom}</span>
        <button className="rounded-md border px-3 py-1 text-sm" onClick={logout}>{t("nav.logout")}</button>
      </header>
      <div className="flex flex-1">
        {open && (
          <button className="fixed inset-0 z-10 bg-black/30 lg:hidden" onClick={() => setOpen(false)} aria-label={t("nav.menu")} />
        )}
        <aside className={`z-20 w-56 shrink-0 border-r bg-white p-3 ${open ? "fixed inset-y-0 left-0 pt-16 lg:static lg:pt-3" : "hidden lg:block"}`}>
          <nav className="space-y-1">
            {links.map((l) => (
              <NavLink key={l.to} to={l.to} end={l.end ?? false} className={itemClass} onClick={() => setOpen(false)}>
                {t(l.key)}
              </NavLink>
            ))}
          </nav>
        </aside>
        <div className="min-w-0 flex-1">
          <Outlet />
        </div>
      </div>
      <footer className="border-t bg-white px-4 py-3 text-center text-xs text-slate-500">
        {t("app.name")}
      </footer>
    </div>
  );
}
