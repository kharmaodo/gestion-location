export function csvCell(v?: string) {
  const s = v ?? "";
  if (/[",\n]/.test(s)) return `"${s.split('"').join('""')}"`;
  return s;
}
