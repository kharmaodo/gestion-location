import { FormEvent, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api, getAccessToken, MeResponse } from "../api";
import { Certificat, Contacts, Contrat, EtatLieux, Resiliation, Restitution, Signature, contratsApi } from "../contrats";
import { locatairesApi } from "../locataires";

const API = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export function ContratDetailPage() {
  const { id } = useParams();
  const [contrat, setContrat] = useState<Contrat | null>(null);
  const [me, setMe] = useState<MeResponse | null>(null);
  const [nomLocataire, setNomLocataire] = useState("");
  const [cert, setCert] = useState<Certificat | null>(null);
  const [contacts, setContacts] = useState<Contacts | null>(null);
  const [resti, setResti] = useState<Restitution | null>(null);
  const [resil, setResil] = useState<Resiliation | null>(null);
  const [preavis, setPreavis] = useState("30");
  const [sigs, setSigs] = useState<Signature[]>([]);
  const [edls, setEdls] = useState<EtatLieux[]>([]);
  const [edlType, setEdlType] = useState("ENTREE");
  const [edlObs, setEdlObs] = useState("");
  const [edlCout, setEdlCout] = useState("0");
  const [role, setRole] = useState("PROPRIETAIRE");
  const [nomSign, setNomSign] = useState("");
  const [motif, setMotif] = useState("Changement de periodicite");
  const [periodicite, setPeriodicite] = useState("MENSUEL");
  const [loyer, setLoyer] = useState("");
  const [dateEffet, setDateEffet] = useState("");
  const [error, setError] = useState<string | null>(null);

  const nomProprio = [me?.prenom, me?.nom].filter(Boolean).join(" ") || me?.email || "";
  const nomLoc = contacts?.locataire?.nom || nomLocataire;
  const jours = Number(preavis) || 0;
  const penaliteEstimee = contrat && jours < 30 ? contrat.loyer : 0;

  async function nomDepuisReservation(reservationId?: string, uniteId?: string) {
    const token = getAccessToken();
    const res = await fetch(`${API}/api/v1/reservations`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });
    if (!res.ok) return;
    const list = (await res.json()) as { id: string; uniteId?: string; prenom?: string; nom?: string }[];
    const r = list.find((x) => x.id === reservationId) ?? list.find((x) => x.uniteId === uniteId);
    if (r) setNomLocataire([r.prenom, r.nom].filter(Boolean).join(" "));
  }

  function reload() {
    if (!id) return;
    contratsApi.get(id).then((c) => {
      setContrat(c);
      if (c.dossierId) {
        locatairesApi.get(c.dossierId).then((d) => {
          setNomLocataire([d.prenom, d.nom].filter(Boolean).join(" "));
        }).catch(() => void nomDepuisReservation(c.reservationId, c.uniteId));
      } else {
        void nomDepuisReservation(c.reservationId, c.uniteId);
      }
    }).catch((e) => setError(e.message));
    contratsApi.contacts(id).then(setContacts).catch(() => setContacts(null));
    contratsApi.signatures(id).then(setSigs).catch(() => setSigs([]));
    contratsApi.etatsLieux(id).then(setEdls).catch(() => setEdls([]));
  }
  useEffect(() => {
    api.me().then(setMe).catch(() => undefined);
    reload();
  }, [id]);

  useEffect(() => {
    setNomSign(role === "PROPRIETAIRE" ? nomProprio : nomLoc);
  }, [role, nomProprio, nomLoc]);

  async function activer() {
    if (!id) return;
    try { await contratsApi.activer(id); reload(); } catch (e) { setError(e instanceof Error ? e.message : "Erreur"); }
  }
  async function resilier() {
    if (!id) return;
    try {
      const r = await contratsApi.resilier(id, jours);
      setResil(r);
      reload();
    } catch (e) { setError(e instanceof Error ? e.message : "Erreur"); }
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
    try { setCert(await contratsApi.certificat(id)); } catch (e) { setError(e instanceof Error ? e.message : "Attestation indisponible"); }
  }
  async function attestationPdf() {
    if (!id) return;
    try { await contratsApi.certificatPdf(id); } catch (e) { setError(e instanceof Error ? e.message : "PDF indisponible"); }
  }
  async function restitution() {
    if (!id) return;
    try { setResti(await contratsApi.restitution(id)); } catch (e) { setError(e instanceof Error ? e.message : "Restitution indisponible"); }
  }
  async function inviter(e: FormEvent) {
    e.preventDefault();
    const nom = (role === "PROPRIETAIRE" ? nomProprio : nomLoc).trim();
    if (!id || !nom) {
      setError(role === "LOCATAIRE" ? "Aucun locataire lie au contrat." : "Profil proprietaire incomplet.");
      return;
    }
    try {
      await contratsApi.inviter({ contratId: id, roleSignataire: role, nomSignataire: nom });
      reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Invitation impossible");
    }
  }
  async function signer(lien?: string) {
    if (!lien) return;
    const token = lien.split("/").pop() ?? lien;
    try {
      await contratsApi.signer(token);
      reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Signature impossible");
    }
  }
  async function creerEdl(e: FormEvent) {
    e.preventDefault();
    if (!id) return;
    try {
      await contratsApi.creerEdl({
        contratId: id,
        type: edlType,
        observations: edlObs,
        coutReparations: Number(edlCout) || 0,
      });
      setEdlObs("");
      reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : "EDL impossible");
    }
  }

  if (!contrat) return <p className="p-8">{error ?? "Chargement..."}</p>;
  return (
    <main className="mx-auto max-w-2xl p-8">
      <Link className="text-sm text-primary" to="/contrats">Contrats</Link>
      <h1 className="mt-2 text-2xl font-semibold text-primary">Contrat {contrat.statut}</h1>
      <p className="text-sm">{contrat.loyer} {contrat.devise} / {contrat.periodicite} · {contrat.dateDebut}</p>
      <p className="mt-2 text-xs text-slate-500">
        Regle de resiliation : preavis inferieur a 30 jours = 1 periode de loyer ({contrat.loyer} {contrat.devise}).
      </p>
      {error && <p className="mt-2 text-sm text-red-600">{error}</p>}
      <div className="mt-4 flex flex-wrap gap-2">
        {contrat.statut === "BROUILLON" && <button className="rounded-md bg-primary px-3 py-2 text-sm text-white" onClick={activer}>Activer</button>}
        <button className="rounded-md border px-3 py-2 text-sm" onClick={attestation}>Attestation</button>
        <button className="rounded-md border px-3 py-2 text-sm" onClick={attestationPdf}>PDF</button>
        <button className="rounded-md border px-3 py-2 text-sm" onClick={restitution}>Restitution caution</button>
      </div>
      {contrat.statut === "ACTIF" && (
        <section className="mt-4 rounded-lg bg-white p-4 text-sm shadow">
          <h2 className="mb-2 font-medium">Resiliation</h2>
          <label className="block">Preavis (jours)
            <input className="ml-2 w-24 rounded-md border px-2 py-1" type="number" min={0} value={preavis} onChange={(e) => setPreavis(e.target.value)} />
          </label>
          <p className="mt-2 text-slate-600">
            Penalite estimee : {penaliteEstimee} {contrat.devise}
            {jours < 30 ? " (preavis inferieur a 30 jours)" : " (preavis respecte)"}
          </p>
          <button className="mt-3 rounded-md border px-3 py-2" onClick={resilier}>Confirmer la resiliation</button>
        </section>
      )}
      {resil && (
        <section className="mt-4 rounded-lg bg-amber-50 p-4 text-sm">
          <p className="font-medium">Contrat resilie</p>
          <p>Penalite : {resil.penalite} {contrat.devise}</p>
          <p>{resil.motifPenalite}</p>
        </section>
      )}
      <section className="mt-4 rounded-lg bg-white p-4 text-sm shadow">
        <h2 className="mb-2 font-medium">Signatures</h2>
        <ul className="space-y-2">
          {sigs.map((s) => (
            <li key={s.id} className="flex items-center justify-between gap-2">
              <span>{s.roleSignataire} · {s.nomSignataire} · {s.statut}</span>
              {s.statut !== "SIGNE" && s.lien && (
                <button className="rounded-md border px-2 py-1 text-xs" onClick={() => signer(s.lien)}>Signer</button>
              )}
            </li>
          ))}
          {sigs.length === 0 && <li className="text-slate-500">Aucune invitation.</li>}
        </ul>
        <form className="mt-3 flex flex-wrap gap-2" onSubmit={inviter}>
          <select className="rounded-md border px-2 py-1" value={role} onChange={(e) => setRole(e.target.value)}>
            <option value="PROPRIETAIRE">Proprietaire</option>
            <option value="LOCATAIRE">Locataire</option>
          </select>
          <input className="min-w-[10rem] flex-1 rounded-md border bg-slate-50 px-2 py-1" value={nomSign} disabled readOnly />
          <button className="rounded-md bg-primary px-3 py-1 text-white" disabled={!nomSign}>Inviter</button>
        </form>
      </section>
      <section className="mt-4 rounded-lg bg-white p-4 text-sm shadow">
        <h2 className="mb-2 font-medium">Etats des lieux</h2>
        <ul className="space-y-2">
          {edls.map((e) => (
            <li key={e.id} className="flex items-center justify-between gap-2">
              <span>{e.type} · {e.statut} · {e.coutReparations ?? 0}</span>
              {e.statut !== "VALIDE" && (
                <button className="rounded-md border px-2 py-1 text-xs" onClick={() => contratsApi.validerEdl(e.id).then(reload).catch((err) => setError(err.message))}>Valider</button>
              )}
            </li>
          ))}
        </ul>
        <form className="mt-3 space-y-2" onSubmit={creerEdl}>
          <select className="w-full rounded-md border px-2 py-1" value={edlType} onChange={(e) => setEdlType(e.target.value)}>
            <option value="ENTREE">Entree</option>
            <option value="SORTIE">Sortie</option>
          </select>
          <input className="w-full rounded-md border px-2 py-1" placeholder="Observations" value={edlObs} onChange={(e) => setEdlObs(e.target.value)} />
          <input className="w-full rounded-md border px-2 py-1" placeholder="Cout reparations" value={edlCout} onChange={(e) => setEdlCout(e.target.value)} />
          <button className="rounded-md bg-primary px-3 py-1 text-white">Creer EDL</button>
        </form>
      </section>
      {contacts && (
        <section className="mt-4 rounded-lg bg-white p-4 text-sm shadow">
          <h2 className="mb-2 font-medium">Contacts</h2>
          {contacts.revele ? (
            <div className="grid gap-3 sm:grid-cols-2">
              <div>
                <p className="text-xs text-slate-500">Proprietaire</p>
                <p>{contacts.proprietaire?.nom ?? "—"}</p>
              </div>
              <div>
                <p className="text-xs text-slate-500">Locataire</p>
                <p>{contacts.locataire?.nom ?? "—"}</p>
              </div>
            </div>
          ) : (
            <p className="text-slate-500">{contacts.motif ?? "Contacts masques."}</p>
          )}
        </section>
      )}
      {resti && (
        <section className="mt-4 rounded-lg bg-white p-4 text-sm shadow">
          <h2 className="mb-2 font-medium">Restitution de caution</h2>
          <p>A restituer : {resti.montantRestitue} {resti.devise}</p>
        </section>
      )}
      {cert && (
        <section className="mt-4 rounded-lg bg-white p-4 text-sm shadow">
          <h2 className="mb-2 font-medium">Attestation</h2>
          <p>{String(cert.texte ?? cert.attestation ?? JSON.stringify(cert))}</p>
        </section>
      )}
      {contrat.statut === "ACTIF" && (
        <form className="mt-6 space-y-3 rounded-lg bg-white p-4 shadow" onSubmit={avenant}>
          <h2 className="font-medium">Avenant</h2>
          <input className="w-full rounded-md border px-3 py-2" value={motif} onChange={(e) => setMotif(e.target.value)} required />
          <select className="w-full rounded-md border px-3 py-2" value={periodicite} onChange={(e) => setPeriodicite(e.target.value)}>
            <option value="JOURNALIER">Journalier</option>
            <option value="HEBDOMADAIRE">Hebdomadaire</option>
            <option value="MENSUEL">Mensuel</option>
          </select>
          <input className="w-full rounded-md border px-3 py-2" placeholder="Nouveau loyer" value={loyer} onChange={(e) => setLoyer(e.target.value)} />
          <input type="date" className="w-full rounded-md border px-3 py-2" value={dateEffet} onChange={(e) => setDateEffet(e.target.value)} required />
          <button className="rounded-md bg-primary px-4 py-2 text-sm text-white">Enregistrer avenant</button>
        </form>
      )}
    </main>
  );
}
