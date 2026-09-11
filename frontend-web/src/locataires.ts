import { getAccessToken } from "./api";

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export type DocumentKyc = {
  id: string;
  type: string;
  nomFichier: string;
  mime?: string;
  kycStatut: string;
};

export type Dossier = {
  id: string;
  prenom?: string;
  nom: string;
  telephone?: string;
  email?: string;
  pieceType?: string;
  pieceNumero?: string;
  kycStatut: string;
  kycCommentaire?: string;
  documents?: DocumentKyc[];
};

async function req<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getAccessToken();
  const res = await fetch(`${API}${path}`, {
    ...init,
    headers: {
      ...(init?.body instanceof FormData ? {} : { "Content-Type": "application/json" }),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(init?.headers ?? {}),
    },
  });
  if (res.status === 204) return undefined as T;
  const body = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(body.detail ?? "Erreur API");
  return body as T;
}

export const locatairesApi = {
  list: () => req<Dossier[]>("/api/v1/locataires"),
  get: (id: string) => req<Dossier>(`/api/v1/locataires/${id}`),
  create: (payload: unknown) => req<Dossier>("/api/v1/locataires", { method: "POST", body: JSON.stringify(payload) }),
  kyc: (id: string, statut: string, commentaire?: string) =>
    req<Dossier>(`/api/v1/locataires/${id}/kyc`, { method: "POST", body: JSON.stringify({ statut, commentaire }) }),
  upload: (id: string, type: string, file: File) => {
    const fd = new FormData();
    fd.append("type", type);
    fd.append("file", file);
    return req<DocumentKyc>(`/api/v1/locataires/${id}/documents`, { method: "POST", body: fd });
  },
};
