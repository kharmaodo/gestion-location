import { getAccessToken } from "./api";

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export type Notification = {
  id: string;
  type: string;
  message: string;
  lu: boolean;
  creeLe: string;
};

async function req<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getAccessToken();
  const res = await fetch(`${API}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  });
  const body = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(body.detail ?? "Erreur API");
  return body as T;
}

export const notificationsApi = {
  list: () => req<Notification[]>("/api/v1/notifications"),
  lu: (id: string) => req<Notification>(`/api/v1/notifications/${id}/lu`, { method: "POST" }),
};
