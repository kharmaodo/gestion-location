const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export type TokenResponse = {
  accessToken?: string;
  refreshToken?: string;
  expiresAt?: string;
  userId?: string;
  roles?: string[];
  requiresTwoFactor?: boolean;
  pendingToken?: string;
};

export type MeResponse = {
  id: string;
  email?: string;
  telephone?: string;
  prenom?: string;
  nom?: string;
  roles: string[];
};

export function saveSession(tokens: TokenResponse) {
  if (tokens.accessToken) sessionStorage.setItem("accessToken", tokens.accessToken);
  if (tokens.refreshToken) sessionStorage.setItem("refreshToken", tokens.refreshToken);
}
export function clearSession() {
  sessionStorage.removeItem("accessToken");
  sessionStorage.removeItem("refreshToken");
}
export function getAccessToken() {
  return sessionStorage.getItem("accessToken");
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getAccessToken();
  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...(init?.headers ?? {}),
  };
  const res = await fetch(`${API}${path}`, { ...init, headers });
  if (res.status === 204 || res.status === 202) return undefined as T;
  const body = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(body.detail ?? "Erreur API");
  return body as T;
}

export const api = {
  register: (payload: unknown) => request<TokenResponse>("/api/v1/auth/register", { method: "POST", body: JSON.stringify(payload) }),
  login: (payload: unknown) => request<TokenResponse>("/api/v1/auth/login", { method: "POST", body: JSON.stringify(payload) }),
  verify2fa: (pendingToken: string, code: string) =>
    request<TokenResponse>("/api/v1/auth/2fa/verify", {
      method: "POST",
      headers: { "X-2FA-Pending": pendingToken },
      body: JSON.stringify({ code }),
    }),
  forgotPassword: (identifiant: string) =>
    request<void>("/api/v1/auth/password/forgot", { method: "POST", body: JSON.stringify({ identifiant }) }),
  resetPassword: (token: string, nouveauMotDePasse: string) =>
    request<void>("/api/v1/auth/password/reset", { method: "POST", body: JSON.stringify({ token, nouveauMotDePasse }) }),
  setup2fa: () => request<{ secret: string; otpauthUrl: string }>("/api/v1/me/2fa/setup", { method: "POST" }),
  enable2fa: (code: string) => request<void>("/api/v1/me/2fa/enable", { method: "POST", body: JSON.stringify({ code }) }),
  disable2fa: (code: string) => request<void>("/api/v1/me/2fa/disable", { method: "POST", body: JSON.stringify({ code }) }),
  me: () => request<MeResponse>("/api/v1/me"),
  logout: () => request<void>("/api/v1/auth/logout", { method: "POST", body: JSON.stringify({ refreshToken: sessionStorage.getItem("refreshToken") }) }),
};
