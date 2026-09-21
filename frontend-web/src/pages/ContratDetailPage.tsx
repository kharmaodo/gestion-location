import { FormEvent, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Certificat, Contacts, Contrat, contratsApi } from "../contrats";

export function ContratDetailPage() {
  const { id } = useParams();
  const [contrat, setContrat] = useState<Contrat | null>(null);
  const [cert, setCert] = useState<Certificat | null>(null);
  const [contacts, setContacts] = useState<Contacts | null>(null);
  const [motif, setMotif] = useState("Changement de periodicite");
  const [periodicite, setPeriodicite] = useState("MENSUEL");
  const [loyer, setLoyer] = useState("");
  const [dateEffet, setDateEffet] = useState("");
  const [error, setError] = useState<string | null>(null);

  function reload() {
    if (!id) return;
    contratsApi.get(id).then(setContrat).catch((e) => setError(e.message));
    contratsApi.contacts(id).then(setContacts).catch(() => setContacts(null));
  }
  useEffect(reload, [id]);

  async function activer() {
    if (!id) return;
    try { await contratsApi.activer(id); reload(); } catch (e) { setError(e instanceof Error ? e.message : "Erreur"); }
  }
  async function resilier() {
    if (!id) return;
    try { await contratsApi.resilier(id); reload(); } catch (e) { setError(e instanceof Error ? e.message : "Erreur"); }
  }
  async function avenant(e: FormEvent) {
    e.preventDefault();
    if (!id) return;
    try {
      await contratsApi.avenant(id, { motif, periodicite, loyer: loyer ? Number(loyer) : null, dateEffet });
      reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur");
    }
  }
  async function attestation() {
    if (!id) return;
    try {
      setCert(await contratsApi.certificat(id));
    } catch (e) {
      setError(e instanceof Error ? e.message : "Attestation indisponible");
    }
  }

  if (!contrat) return <p className="p-8">{error ?? "Chargement..."}</p>;
  return (
    <main className="mx-auto max-w-2xl p-8">
      <Link className="text-sm text-primary" to="/contrats">Contrats</Link>
      <h1 className="mt-2 text-2xl font-semibold text-primary">Contrat {contrat.statut}</h1>
      <p className="text-sm">{contrat.loyer} {contrat.devise} / {contrat.periodicite} · {contrat.dateDebut}</p>
      {error && <p className="mt-2 text-sm text-red-600">{error}</p>}
      <div className="mt-4 flex flex-wrap gap-2">
        {contrat.statut === "BROUILLON" && <button className="rounded-md bg-primary px-3 py-2 text-sm text-white" onClick={activer}>Activer</button>}
        {contrat.statut === "ACTIF" && <button className="rounded-md border px-3 py-2 text-sm" onClick={resilier}>Resilier</button>}
        <button className="rounded-md border px-3 py-2 text-sm" onClick={attestation}>Attestation</button>
      </div>
      {contacts && (
        <section className="mt-4 rounded-lg bg-white p-4 text-sm shadow">
          <h2 className="mb-2 font-medium">Contacts</h2>
          {contacts.revele ? (
            <div className="grid gap-3 sm:grid-cols-2">
              <div>
                <p className="text-xs text-slate-500">Proprietaire</p>
                <p>{contacts.proprietaire?.nom ?? "—"}</p>
                <p>{contacts.proprietaire?.email ?? "—"}</p>
                <p>{contacts.proprietaire?.telephone ?? "—"}</p>
              </div>
              <div>
                <p className="text-xs text-slate-500">Locataire</p>
                <p>{contacts.locataire?.nom ?? "—"}</p>
                <p>{contacts.locataire?.email ?? "—"}</p>
                <p>{contacts.locataire?.telephone ?? "—"}</p>
              </div>
            </div>
          ) : (
            <p className="text-slate-500">{contacts.motif ?? "Contacts masques tant que le contrat n'est pas signe des deux cotes."}</p>
          )}
        </section>
      )}
      {cert && (
        <section className="mt-4 rounded-lg bg-white p-4 text-sm shadow">
          <h2 className="mb-2 font-medium">Attestation de location</h2>
          <p>{String(cert.attestation ?? JSON.stringify(cert, null, 2))}</p>
          <p className="mt-2 text-slate-600">{cert.statut} · {cert.loyer} {cert.devise} / {cert.periodicite}</p>
          <p className="text-slate-500">{cert.dateDebut} → {cert.dateFin ?? "—"}</p>
        </section>
      )}
      <ul className="mt-6 space-y-2 text-sm">
        {(contrat.avenants ?? []).map((a) => (
          <li key={a.id} className="rounded-lg bg-white p-3 shadow">{a.dateEffet} — {a.motif} {a.periodicite ?? ""} {a.loyer ?? ""}</li>
        ))}
      </ul>
      {contrat.statut === "ACTIF" && (
        <form className="mt-6 space-y-3 rounded-lg bg-white p-4 shadow" onSubmit={avenant}>
          <h2 className="font-medium">Avenant</h2>
          <input className="w-full rounded-md border px-3 py-2" value={motif} onChange={(e) => setMotif(e.target.value)} required />
          <select className="w-full rounded-md border px-3 py-2" value={periodicite} onChange={(e) => setPeriodicite(e.target.value)}>
            <option value="JOURNALIER">Journalier</option>
            <option value="HEBDOMADAIRE">Hebdomadaire</option>
            <option value="MENSUEL">Mensuel</option>
          </select>
          <input className="w-full rounded-md border px-3 py-2" placeholder="Nouveau loyer (optionnel)" value={loyer} onChange={(e) => setLoyer(e.target.value)} />
          <input type="date" className="w-full rounded-md border px-3 py-2" value={dateEffet} onChange={(e) => setDateEffet(e.target.value)} required />
          <button className="rounded-md bg-primary px-4 py-2 text-sm text-white">Enregistrer avenant</button>
        </form>
      )}
    </main>
  );
}
