const host = process.env.BACKEND_URL || "http://localhost:8080/";
const reservationsEndpoint = "api/v1/reservations";
const eventsEndpoint = "api/v1/events";

const locationsEndpoint = "api/v1/locations";
const usersEndpoint = "api/v1/users";
const confirmationTokenEndpoint = "api/v1/confirmations";
const recurrentEventsEndpoint = "api/v1/events/recurrent";
const accessTokenEndpoint = "api/v1/token";
const refreshTokenEndpoint = "api/token/refresh";
const resendEmailEndpoint = "api/v1/confirmations/resend-confirmation";

const passwordResetEndpoint = "api/v1/password-reset";
const submitConfirmationsTokenEndpoint = "api/v1/confirmations/submit-token";



export {
  host,
  accessTokenEndpoint,
  eventsEndpoint,
  reservationsEndpoint,
  locationsEndpoint,
  resendEmailEndpoint,
  recurrentEventsEndpoint,
  refreshTokenEndpoint,
  usersEndpoint,
  confirmationTokenEndpoint,
  submitConfirmationsTokenEndpoint,
  passwordResetEndpoint,
};
