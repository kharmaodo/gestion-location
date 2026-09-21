import { getAccessToken } from "./api";

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export type Litige = {
  id: string;
  contratId: string;
  auteurId: string;
  motif: string;
  description: string;
  statut: string;
  decision?: string;
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

export const litigesApi = {
  list: () => req<Litige[]>("/api/v1/litiges"),
  ouvrir: (payload: { contratId: string; motif: string; description: string }) =>
    req<Litige>("/api/v1/litiges", { method: "POST", body: JSON.stringify(payload) }),
  decider: (id: string, statut: string, decision: string) =>
    req<Litige>(`/api/v1/litiges/${id}/decision`, {
      method: "POST",
      body: JSON.stringify({ statut, decision }),
    }),
};
