export type NavLink = { to: string; key: string; end?: boolean };

export const PRO_LINKS: NavLink[] = [
  { to: "/", key: "nav.home", end: true },
  { to: "/biens", key: "nav.biens" },
  { to: "/locataires", key: "nav.locataires" },
  { to: "/contrats", key: "nav.contrats" },
  { to: "/caution", key: "nav.caution" },
  { to: "/loyers", key: "nav.loyers" },
  { to: "/litiges", key: "nav.litiges" },
  { to: "/visites", key: "nav.visites" },
  { to: "/reservations", key: "nav.reservations" },
  { to: "/messages", key: "nav.messages" },
  { to: "/notifications", key: "nav.notifications" },
  { to: "/annonces", key: "nav.annonces" },
  { to: "/securite", key: "nav.securite" },
];

export const LOC_LINKS: NavLink[] = [
  { to: "/", key: "nav.home", end: true },
  { to: "/annonces", key: "nav.annonces" },
  { to: "/contrats", key: "nav.contrats" },
  { to: "/caution", key: "nav.caution" },
  { to: "/litiges", key: "nav.litiges" },
  { to: "/messages", key: "nav.messages" },
  { to: "/notifications", key: "nav.notifications" },
  { to: "/securite", key: "nav.securite" },
];

export function linksFor(roles: string[] = []) {
  return roles.includes("PROPRIETAIRE") ? PRO_LINKS : LOC_LINKS;
}

export function translate(messages: Record<string, string>, key: string) {
  return messages[key] ?? key;
}
