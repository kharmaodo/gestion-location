import { FormEvent, useEffect, useState } from "react";
import { Link, NavLink, Outlet, useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { api, clearSession, MeResponse } from "../api";
import { useI18n } from "../i18n";

const PRO_LINKS = [
  { to: "/", key: "Tableau de bord", end: true },
  { to: "/biens", key: "nav.biens" },
  { to: "/locataires", key: "nav.locataires" },
  { to: "/contrats", key: "nav.contrats" },
  { to: "/loyers", key: "nav.loyers" },
  { to: "/visites", key: "nav.visites" },
  { to: "/reservations", key: "Reservations" },
  { to: "/messages", key: "nav.messages" },
  { to: "/annonces", key: "Annonces" },
];

const LOC_LINKS = [
  { to: "/", key: "Tableau de bord", end: true },
  { to: "/annonces", key: "Annonces" },
  { to: "/messages", key: "nav.messages" },
  { to: "/securite", key: "Securite" },
];

export function AppShell() {
  const navigate = useNavigate();
  const location = useLocation();
  const [params] = useSearchParams();
  const { t, locale, setLocale } = useI18n();
  const [me, setMe] = useState<MeResponse | null>(null);
  const [q, setQ] = useState(params.get("q") ?? "");
  const [open, setOpen] = useState(false);

  useEffect(() => {
    api.me().then(setMe).catch(() => {
      clearSession();
      navigate("/connexion");
    });
  }, [navigate]);

  const proprio = me?.roles.includes("PROPRIETAIRE") ?? false;
  const links = proprio ? PRO_LINKS : LOC_LINKS;
  const nom = [me?.prenom, me?.nom].filter(Boolean).join(" ") || me?.email || "…";

  function onSearch(e: FormEvent) {
    e.preventDefault();
    const query = q.trim();
    const target = proprio
      ? location.pathname.startsWith("/locataires")
        ? "/locataires"
        : location.pathname.startsWith("/contrats")
          ? "/contrats"
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

  const menu = (
    <nav className="space-y-1">
      {links.map((l) => (
        <NavLink key={l.to} to={l.to} end={"end" in l ? l.end : false} className={itemClass} onClick={() => setOpen(false)}>
          {l.key.startsWith("nav.") || l.key === "app.name" ? t(l.key) : l.key}
        </NavLink>
      ))}
    </nav>
  );

  return (
    <div className="flex min-h-screen flex-col bg-slate-50">
      <header className="sticky top-0 z-20 flex items-center gap-3 border-b bg-white px-4 py-3">
        <button className="rounded-md border px-2 py-1 text-sm lg:hidden" onClick={() => setOpen((o) => !o)}>
          Menu
        </button>
        <Link to="/" className="font-semibold text-primary">{t("app.name")}</Link>
        <form className="mx-auto hidden max-w-xl flex-1 md:block" onSubmit={onSearch}>
          <input
            className="w-full rounded-md border px-3 py-1.5 text-sm"
            placeholder="Rechercher…"
            value={q}
            onChange={(e) => setQ(e.target.value)}
          />
        </form>
        <select className="rounded-md border px-2 py-1 text-sm" value={locale} onChange={(e) => setLocale(e.target.value)}>
          <option value="fr">FR</option>
          <option value="en">EN</option>
          <option value="wo">WO</option>
        </select>
        <span className="hidden text-sm font-medium sm:inline">{nom}</span>
        <button className="rounded-md border px-3 py-1 text-sm" onClick={logout}>Sortir</button>
      </header>
      <div className="flex flex-1">
        {open && (
          <button className="fixed inset-0 z-10 bg-black/30 lg:hidden" onClick={() => setOpen(false)} aria-label="Fermer" />
        )}
        <aside className={`z-20 w-56 shrink-0 border-r bg-white p-3 ${open ? "fixed inset-y-0 left-0 pt-16 lg:static lg:pt-3" : "hidden lg:block"}`}>
          {menu}
          <NavLink to="/securite" className={itemClass} onClick={() => setOpen(false)}>Securite</NavLink>
        </aside>
        <div className="min-w-0 flex-1">
          <Outlet />
        </div>
      </div>
      <footer className="border-t bg-white px-4 py-3 text-center text-xs text-slate-500">
        {t("app.name")} · Donnees personnelles · Aide
      </footer>
    </div>
  );
}
