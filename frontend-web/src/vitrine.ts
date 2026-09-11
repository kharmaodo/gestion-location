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

async function req<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API}${path}`, {
    ...init,
    headers: { "Content-Type": "application/json", ...(init?.headers ?? {}) },
  });
  const body = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(body.detail ?? "Erreur API");
  return body as T;
}

export const vitrineApi = {
  list: (ville?: string) => req<Annonce[]>(`/api/v1/public/annonces${ville ? `?ville=${encodeURIComponent(ville)}` : ""}`),
  get: (id: string) => req<Annonce>(`/api/v1/public/annonces/${id}`),
  dispo: (id: string) => req<Creneau[]>(`/api/v1/public/annonces/${id}/disponibilites`),
  reserver: (payload: unknown) => req(`/api/v1/public/reservations`, { method: "POST", body: JSON.stringify(payload) }),
};
