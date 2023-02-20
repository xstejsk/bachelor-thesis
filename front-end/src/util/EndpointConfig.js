const host = "http://localhost:8080/";
const loginEndpoint = "api/auth/login";
const loggedInUserEndpoint = "api/users/logged-user";
const eventsEndpoint = "api/events";
const reservationsEndpoint = "api/reservations";
const newEventEndpoint = "api/events/create";
const newReservationEndpoint = "api/reservations/create";
const newLocationEndpoint = "api/locations/create";
const locationsEndpoint = "api/locations";
const activeEventsEndpoint = "api/events/active";
const cancelEventById = "api/events/cancel/";
const cancelEventsByGroupId = "api/events/cancel-recurrent/";
const registerEndpoint = "api/auth/register";
const confirmRegistrationEndopoint = "api/auth/confirm-registration/";
const resendEmailEndpoint = "api/auth/{emailAddress}/resend-confirmation-email";
const logoutEndpoint = "api/auth/logout";
const forgotPasswordEndpoint = "api/access/{email}/forgot-password";
const resetPasswordEdnpoint = "api/access/reset-password/";
const activeReservationsByUser = "api/reservations/{userId}/active";
const cancelMultipleReservationsEndpoint = reservationsEndpoint + "/cancel";
const getAllUsersEndpoint = "api/users";
const updateEventEndpoint = "api/events/update/";
const updateRecurrenceGroup = "api/events/update-recurrent/";

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
  cancelMultipleReservationsEndpoint,
  getAllUsersEndpoint,
  updateEventEndpoint,
  updateRecurrenceGroup,
  activeReservationsByUser,
};
