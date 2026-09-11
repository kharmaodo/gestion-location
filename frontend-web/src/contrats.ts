import { getAccessToken } from "./api";

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export type Avenant = {
  id: string;
  motif: string;
  periodicite?: string;
  loyer?: number;
  dateEffet: string;
};

export type Contrat = {
  id: string;
  uniteId: string;
  dossierId?: string;
  reservationId?: string;
  dateDebut: string;
  dateFin?: string;
  loyer: number;
  devise: string;
  periodicite: string;
  jourEcheance?: number;
  caution?: number;
  statut: string;
  avenants?: Avenant[];
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

export const contratsApi = {
  list: () => req<Contrat[]>("/api/v1/contrats"),
  get: (id: string) => req<Contrat>(`/api/v1/contrats/${id}`),
  create: (payload: unknown) => req<Contrat>("/api/v1/contrats", { method: "POST", body: JSON.stringify(payload) }),
  activer: (id: string) => req<Contrat>(`/api/v1/contrats/${id}/activation`, { method: "POST" }),
  resilier: (id: string) => req<Contrat>(`/api/v1/contrats/${id}/resiliation`, { method: "POST" }),
  avenant: (id: string, payload: unknown) =>
    req<Contrat>(`/api/v1/contrats/${id}/avenants`, { method: "POST", body: JSON.stringify(payload) }),
};
