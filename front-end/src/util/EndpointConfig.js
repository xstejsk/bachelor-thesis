const host = "http://localhost:8080/";
const loginEndpoint = "api/auth/login";
const loggedInUserEndpoint = "api/user";
const eventsEndpoint = "api/events/all";
const reservationsEndpoint = "api/reservations/all";
const newEventEndpoint = "api/events/new";
const newReservationEndpoint = "api/reservations/new";
const newLocationEndpoint = "api/locations/new";
const locationsEndpoint = "api/locations/all";
const activeEventsEndpoint = "api/events/active";
const cancelEventById = "api/events/cancel/";
const cancelEventsByGroupId = "api/events/cancel/group/";
const registerEndpoint = "api/auth/register";
const confirmRegistrationEndopoint = "api/auth/registration/confirm/";
const resendEmailEndpoint = "api/auth/registration/resend/";
const logoutEndpoint = "api/auth/logout";
const forgotPasswordEndpoint = "api/access/{email}/forgot-password";
const resetPasswordEdnpoint = "api/access/reset-password/";

export {
  host,
  loginEndpoint,
  registerEndpoint,
  loggedInUserEndpoint,
  eventsEndpoint,
  reservationsEndpoint,
  newEventEndpoint,
  newReservationEndpoint,
  newLocationEndpoint,
  locationsEndpoint,
  activeEventsEndpoint,
  cancelEventById,
  cancelEventsByGroupId,
  confirmRegistrationEndopoint,
  resendEmailEndpoint,
  logoutEndpoint,
  forgotPasswordEndpoint,
  resetPasswordEdnpoint,
};
