const host = process.env.BACKEND_URL || "http://localhost:8080/";
const loginEndpoint = "api/auth/login";
const loggedInUserEndpoint = "api/users/logged-user";
const eventsEndpoint = "api/events";
const reservationsEndpoint = "api/reservations";
const newEventEndpoint = "api/events/create";
const newReservationEndpoint = "api/reservations/create";
const newLocationEndpoint = "api/locations/create";
const locationsEndpoint = "api/locations";
const activeEventsEndpoint = "api/events";
const cancelEventById = "api/events/delete/";
const cancelEventsByGroupId = "api/events/delete-recurrent/";
const registerEndpoint = "api/auth/register";
const confirmRegistrationEndopoint = "api/auth/confirm-registration/";
const resendEmailEndpoint = "api/auth/{emailAddress}/resend-confirmation-email";
const logoutEndpoint = "api/auth/logout";
const forgotPasswordEndpoint = "api/access/{email}/forgot-password";
const resetPasswordEdnpoint = "api/access/reset-password/";
const reservationsByUser = "api/reservations/{userId}";
const getAllUsersEndpoint = "api/users";
const updateEventEndpoint = "api/events/update/";
const updateRecurrenceGroup = "api/events/update-recurrent/";
const deleteReservation = "api/reservations/delete/";
const promoteUser = "api/users/promote/";
const banUser = "api/users/ban/";
const unbanUser = "api/users/unban/";
const deleteUser = "api/users/delete/";
const deleteCalendarEndpoint = "api/locations/delete/";
const refreshTokenEndpoint = "api/token/refresh";

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
  getAllUsersEndpoint,
  updateEventEndpoint,
  updateRecurrenceGroup,
  reservationsByUser,
  deleteReservation,
  promoteUser,
  banUser,
  unbanUser,
  deleteUser,
  deleteCalendarEndpoint,
  refreshTokenEndpoint,
};
