import { useEffect, useState } from "react";
import { Notification, notificationsApi } from "../notifications";

export function NotificationsPage() {
  const [items, setItems] = useState<Notification[]>([]);
  const [error, setError] = useState<string | null>(null);

  function reload() {
    notificationsApi.list().then(setItems).catch((e) => setError(e.message));
  }
  useEffect(reload, []);

  async function mark(id: string) {
    try {
      await notificationsApi.lu(id);
      reload();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erreur");
    }
  }

  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="mb-6 text-2xl font-semibold text-primary">Notifications</h1>
      {error && <p className="mb-3 text-sm text-red-600">{error}</p>}
      <div className="space-y-2">
        {items.map((n) => (
          <div key={n.id} className={`rounded-lg p-4 shadow ${n.lu ? "bg-white" : "bg-amber-50"}`}>
            <div className="flex items-start justify-between gap-3">
              <div>
                <p className="text-xs uppercase text-slate-500">{n.type}</p>
                <p className="text-sm">{n.message}</p>
                <p className="mt-1 text-xs text-slate-400">{n.creeLe}</p>
              </div>
              {!n.lu && (
                <button className="shrink-0 rounded-md border px-2 py-1 text-xs" onClick={() => mark(n.id)}>
                  Marquer lu
                </button>
              )}
            </div>
          </div>
        ))}
        {items.length === 0 && !error && <p className="text-sm text-slate-500">Aucune notification.</p>}
      </div>
    </main>
  );
}
