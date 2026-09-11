import { Navigate, Route, Routes } from "react-router-dom";
import { getAccessToken } from "./api";
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
      <Route path="/" element={<PrivateRoute><HomePage /></PrivateRoute>} />
      <Route path="/securite" element={<PrivateRoute><SecurityPage /></PrivateRoute>} />
      <Route path="/biens" element={<PrivateRoute><BiensPage /></PrivateRoute>} />
      <Route path="/biens/nouveau" element={<PrivateRoute><NouveauBienPage /></PrivateRoute>} />
      <Route path="/biens/:id" element={<PrivateRoute><BienDetailPage /></PrivateRoute>} />
      <Route path="/locataires" element={<PrivateRoute><LocatairesPage /></PrivateRoute>} />
      <Route path="/locataires/nouveau" element={<PrivateRoute><NouveauDossierPage /></PrivateRoute>} />
      <Route path="/locataires/:id" element={<PrivateRoute><DossierDetailPage /></PrivateRoute>} />
      <Route path="/reservations" element={<PrivateRoute><ReservationsPage /></PrivateRoute>} />
      <Route path="/contrats" element={<PrivateRoute><ContratsPage /></PrivateRoute>} />
      <Route path="/contrats/nouveau" element={<PrivateRoute><NouveauContratPage /></PrivateRoute>} />
      <Route path="/contrats/:id" element={<PrivateRoute><ContratDetailPage /></PrivateRoute>} />
      <Route path="/loyers" element={<PrivateRoute><LoyersPage /></PrivateRoute>} />
      <Route path="/messages" element={<PrivateRoute><ConversationsPage /></PrivateRoute>} />
      <Route path="/messages/:id" element={<PrivateRoute><ConversationDetailPage /></PrivateRoute>} />
    </Routes>
  );
}
