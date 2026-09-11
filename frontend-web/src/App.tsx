import { Navigate, Route, Routes } from "react-router-dom";
import { getAccessToken } from "./api";
import { BienDetailPage } from "./pages/BienDetailPage";
import { BiensPage } from "./pages/BiensPage";
import { ForgotPage } from "./pages/ForgotPage";
import { HomePage } from "./pages/HomePage";
import { LoginPage } from "./pages/LoginPage";
import { NouveauBienPage } from "./pages/NouveauBienPage";
import { RegisterPage } from "./pages/RegisterPage";
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
      <Route path="/" element={<PrivateRoute><HomePage /></PrivateRoute>} />
      <Route path="/securite" element={<PrivateRoute><SecurityPage /></PrivateRoute>} />
      <Route path="/biens" element={<PrivateRoute><BiensPage /></PrivateRoute>} />
      <Route path="/biens/nouveau" element={<PrivateRoute><NouveauBienPage /></PrivateRoute>} />
      <Route path="/biens/:id" element={<PrivateRoute><BienDetailPage /></PrivateRoute>} />
    </Routes>
  );
}
