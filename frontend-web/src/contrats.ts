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

export type Certificat = {
  contratId: string;
  statut: string;
  dateDebut: string;
  dateFin?: string;
  loyer: number;
  devise: string;
  periodicite: string;
  attestation?: string;
  [key: string]: unknown;
};

export type ContactPartie = { nom?: string; email?: string; telephone?: string };
export type Contacts = {
  revele: boolean;
  motif?: string;
  proprietaire?: ContactPartie;
  locataire?: ContactPartie;
};

export type Restitution = {
  contratId: string;
  cautionInitiale: number;
  coutReparations: number;
  montantRetenu: number;
  montantRestitue: number;
  edlSortieValide: boolean;
  devise: string;
};

export type Signature = {
  id: string;
  contratId: string;
  roleSignataire: string;
  nomSignataire: string;
  statut: string;
  signeLe?: string;
  lien?: string;
};

export type EtatLieux = {
  id: string;
  contratId: string;
  type: string;
  observations?: string;
  coutReparations?: number;
  statut: string;
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

export const contratsApi = {
  list: () => req<Contrat[]>("/api/v1/contrats"),
  mes: () => req<Contrat[]>("/api/v1/mes-contrats"),
  get: (id: string) => req<Contrat>(`/api/v1/contrats/${id}`),
  create: (payload: unknown) => req<Contrat>("/api/v1/contrats", { method: "POST", body: JSON.stringify(payload) }),
  activer: (id: string) => req<Contrat>(`/api/v1/contrats/${id}/activation`, { method: "POST" }),
  resilier: (id: string) => req<Contrat>(`/api/v1/contrats/${id}/resiliation`, { method: "POST" }),
  avenant: (id: string, payload: unknown) =>
    req<Contrat>(`/api/v1/contrats/${id}/avenants`, { method: "POST", body: JSON.stringify(payload) }),
  certificat: (id: string) => req<Certificat>(`/api/v1/contrats/${id}/certificat`),
  contacts: (id: string) => req<Contacts>(`/api/v1/contrats/${id}/contacts`),
  restitution: (id: string) => req<Restitution>(`/api/v1/contrats/${id}/restitution-caution`),
  signatures: (contratId: string) => req<Signature[]>(`/api/v1/signatures?contratId=${contratId}`),
  inviter: (payload: { contratId: string; roleSignataire: string; nomSignataire: string }) =>
    req<Signature>("/api/v1/signatures", { method: "POST", body: JSON.stringify(payload) }),
  signer: (token: string) => req<Signature>(`/api/v1/public/signatures/${token}`, { method: "POST" }),
  etatsLieux: (contratId: string) => req<EtatLieux[]>(`/api/v1/etats-lieux?contratId=${contratId}`),
  creerEdl: (payload: { contratId: string; type: string; observations?: string; coutReparations?: number }) =>
    req<EtatLieux>("/api/v1/etats-lieux", { method: "POST", body: JSON.stringify(payload) }),
  validerEdl: (id: string) => req<EtatLieux>(`/api/v1/etats-lieux/${id}/validation`, { method: "POST" }),
};
