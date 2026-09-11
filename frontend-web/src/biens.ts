import { getAccessToken } from "./api";

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export type Unite = {
  id: string;
  bienId: string;
  libelle: string;
  type: string;
  surfaceM2?: number;
  meuble: boolean;
  loyer: number;
  devise: string;
  periodicite: string;
  jourEcheance?: number;
  statut: string;
  publie: boolean;
};

export type Bien = {
  id: string;
  designation: string;
  type: string;
  adresse?: string;
  ville?: string;
  statut: string;
  unites: number;
  unitesLibres: number;
  unitesDetail?: Unite[];
};

async function req<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getAccessToken();
  const res = await fetch(`${API}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(init?.headers ?? {}),
    },
  });
  if (res.status === 204) return undefined as T;
  const body = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(body.detail ?? "Erreur API");
  return body as T;
}

export const biensApi = {
  list: () => req<Bien[]>("/api/v1/biens"),
  get: (id: string) => req<Bien>(`/api/v1/biens/${id}`),
  create: (payload: unknown) => req<Bien>("/api/v1/biens", { method: "POST", body: JSON.stringify(payload) }),
  addUnite: (bienId: string, payload: unknown) =>
    req<Unite>(`/api/v1/biens/${bienId}/unites`, { method: "POST", body: JSON.stringify(payload) }),
  setPeriodicite: (bienId: string, uniteId: string, payload: unknown) =>
    req<Unite>(`/api/v1/biens/${bienId}/unites/${uniteId}/periodicite`, {
      method: "PUT",
      body: JSON.stringify(payload),
    }),
  publier: (bienId: string, uniteId: string, publie: boolean) =>
    req<Unite>(`/api/v1/biens/${bienId}/unites/${uniteId}/publication`, {
      method: "PUT",
      body: JSON.stringify({ publie }),
    }),
};
