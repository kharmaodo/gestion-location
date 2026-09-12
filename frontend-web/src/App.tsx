import { Navigate, Route, Routes } from "react-router-dom";
import { getAccessToken } from "./api";
import { AppShell } from "./layout/AppShell";
import { AnnonceDetailPage } from "./pages/AnnonceDetailPage";
import { AnnoncesPage } from "./pages/AnnoncesPage";
import { BienDetailPage } from "./pages/BienDetailPage";
import { BiensPage } from "./pages/BiensPage";
import { ContratDetailPage } from "./pages/ContratDetailPage";
import { ContratsPage } from "./pages/ContratsPage";
import { ConversationDetailPage } from "./pages/ConversationDetailPage";
import { ConversationsPage } from "./pages/ConversationsPage";
import { DossierDetailPage } from "./pages/DossierDetailPage";
import { ForgotPage } from "./pages/ForgotPage";
import { HomePage } from "./pages/HomePage";
import { LocatairesPage } from "./pages/LocatairesPage";
import { LoginPage } from "./pages/LoginPage";
import { LoyersPage } from "./pages/LoyersPage";
import { NouveauBienPage } from "./pages/NouveauBienPage";
import { NouveauContratPage } from "./pages/NouveauContratPage";
import { NouveauDossierPage } from "./pages/NouveauDossierPage";
import { RegisterPage } from "./pages/RegisterPage";
import { ReservationsPage } from "./pages/ReservationsPage";
import { ResetPage } from "./pages/ResetPage";
import { SecurityPage } from "./pages/SecurityPage";
import { VisitesPage } from "./pages/VisitesPage";

function PrivateRoute({ children }: { children: JSX.Element }) {
  return getAccessToken() ? children : <Navigate to="/connexion" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/connexion" element={<LoginPage />} />
      <Route path="/inscription" element={<RegisterPage />} />
      <Route path="/mot-de-passe-oublie" element={<ForgotPage />} />
      <Route path="/reset-mot-de-passe" element={<ResetPage />} />
      <Route path="/annonces" element={<AnnoncesPage />} />
      <Route path="/annonces/:id" element={<AnnonceDetailPage />} />
      <Route
        element={
          <PrivateRoute>
            <AppShell />
          </PrivateRoute>
        }
      >
        <Route path="/" element={<HomePage />} />
        <Route path="/securite" element={<SecurityPage />} />
        <Route path="/biens" element={<BiensPage />} />
        <Route path="/biens/nouveau" element={<NouveauBienPage />} />
        <Route path="/biens/:id" element={<BienDetailPage />} />
        <Route path="/locataires" element={<LocatairesPage />} />
        <Route path="/locataires/nouveau" element={<NouveauDossierPage />} />
        <Route path="/locataires/:id" element={<DossierDetailPage />} />
        <Route path="/reservations" element={<ReservationsPage />} />
        <Route path="/contrats" element={<ContratsPage />} />
        <Route path="/contrats/nouveau" element={<NouveauContratPage />} />
        <Route path="/contrats/:id" element={<ContratDetailPage />} />
        <Route path="/loyers" element={<LoyersPage />} />
        <Route path="/messages" element={<ConversationsPage />} />
        <Route path="/messages/:id" element={<ConversationDetailPage />} />
        <Route path="/visites" element={<VisitesPage />} />
      </Route>
    </Routes>
  );
}
