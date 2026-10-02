import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  scenarios: {
    sante: {
      executor: "constant-vus",
      vus: 10,
      duration: "20s",
    },
  },
  thresholds: {
    checks: ["rate>0.99"],
    http_req_duration: ["p(95)<800"],
  },
};

const API = __ENV.API || "http://localhost:8080";

export default function () {
  const res = http.get(`${API}/actuator/health`);
  check(res, { "health 200": (r) => r.status === 200 });
  const login = http.post(
    `${API}/api/v1/auth/login`,
    JSON.stringify({ identifiant: "inconnu@test.sn", motDePasse: "x" }),
    { headers: { "Content-Type": "application/json" }, responseCallback: http.expectedStatuses(401, 429) }
  );
  check(login, { "login refuse": (r) => r.status === 401 || r.status === 429 });
  sleep(0.2);
}
