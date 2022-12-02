import authService from "./auth-service";

export default function authHeader() {
  const jwt = authService.getUserJwt();
  if (jwt) {
    return { Authorization: "Bearer " + jwt };
  } else {
    return {};
  }
}
