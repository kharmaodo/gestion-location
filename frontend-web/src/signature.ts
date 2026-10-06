export function signatureLibelle(mode?: string) {
  if (mode === "LIVE") return "Signature live non branchee";
  if (mode === "OFF") return "Signature desactivee";
  return "Signature mock";
}
