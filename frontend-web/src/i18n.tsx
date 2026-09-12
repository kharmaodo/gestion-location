import { createContext, useContext, useEffect, useMemo, useState } from "react";

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

type Catalog = { locale: string; messages: Record<string, string> };

const I18nCtx = createContext<{
  locale: string;
  t: (key: string) => string;
  setLocale: (lang: string) => void;
}>({ locale: "fr", t: (k) => k, setLocale: () => undefined });

export function I18nProvider({ children }: { children: React.ReactNode }) {
  const [locale, setLocaleState] = useState(() => localStorage.getItem("lang") ?? "fr");
  const [messages, setMessages] = useState<Record<string, string>>({});

  useEffect(() => {
    localStorage.setItem("lang", locale);
    fetch(`${API}/api/v1/public/i18n?lang=${locale}`)
      .then((r) => r.json())
      .then((c: Catalog) => setMessages(c.messages ?? {}))
      .catch(() => setMessages({}));
  }, [locale]);

  const value = useMemo(
    () => ({
      locale,
      t: (key: string) => messages[key] ?? key,
      setLocale: setLocaleState,
    }),
    [locale, messages]
  );

  return <I18nCtx.Provider value={value}>{children}</I18nCtx.Provider>;
}

export function useI18n() {
  return useContext(I18nCtx);
}
