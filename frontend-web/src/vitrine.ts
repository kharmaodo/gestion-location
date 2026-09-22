import { getAccessToken } from "./api";

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export type Annonce = {
  uniteId: string;
  bienId: string;
  designationBien: string;
  ville?: string;
  adresse?: string;
  libelle: string;
  type: string;
  surfaceM2?: number;
  meuble: boolean;
  loyer: number;
  devise: string;
  periodicite: string;
  statut: string;
};

export type Creneau = { debut: string; fin: string; statut: string };
export type Media = { id?: string; url: string; type?: string };

export type Avis = {
  id: string;
  auteurId: string;
  cibleUniteId: string;
  note: number;
  commentaire?: string;
  creeLe: string;
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
  const body = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(body.detail ?? "Erreur API");
  return body as T;
}

export const vitrineApi = {
  list: (ville?: string) => req<Annonce[]>(`/api/v1/public/annonces${ville ? `?ville=${encodeURIComponent(ville)}` : ""}`),
  get: (id: string) => req<Annonce>(`/api/v1/public/annonces/${id}`),
  medias: (id: string) => req<Media[]>(`/api/v1/public/annonces/${id}/medias`),
  dispo: (id: string) => req<Creneau[]>(`/api/v1/public/annonces/${id}/disponibilites`),
  reserver: (payload: unknown) => req(`/api/v1/public/reservations`, { method: "POST", body: JSON.stringify(payload) }),
  visiter: (payload: { uniteId: string; nom: string; telephone?: string; creneau: string }) =>
    req(`/api/v1/public/visites`, { method: "POST", body: JSON.stringify(payload) }),
  avis: (id: string) => req<Avis[]>(`/api/v1/public/annonces/${id}/avis`),
  publierAvis: (payload: { cibleUniteId: string; note: number; commentaire?: string }) =>
    req<Avis>("/api/v1/avis", { method: "POST", body: JSON.stringify(payload) }),
};
