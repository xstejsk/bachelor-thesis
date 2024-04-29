import axiosInstance from "./base";
import {
  NewCalendar,
  Calendar,
  CalendarWithEvents,
  NewEvent,
  Event,
  Reservation,
  UpdateCalendarRequest,
} from "../models/calendar";
import { Page } from "../models/common";

const create = async (calendar: NewCalendar): Promise<Calendar> => {
  const response = await axiosInstance.post("/api/v1/calendars", calendar);
  return response.data;
};

const update = async (calendar: UpdateCalendarRequest): Promise<Calendar> => {
  const response = await axiosInstance.patch(
    `/api/v1/calendars/${calendar.id}`,
    {
      name: calendar.name,
      locationId: calendar.locationId,
      thumbnail: calendar.thumbnail,
    }
  );
  return response.data;
};

const getCalendarWithEvents = async (
  calendarId: string,
  eventsFrom: string,
  eventsTo: string
): Promise<CalendarWithEvents> => {
  const response = await axiosInstance.get(
    `/api/v1/calendars/${calendarId}?eventsFrom=${eventsFrom}&eventsTo=${eventsTo}`
  );
  return response.data;
};

const createEvent = async (
  calendarId: string,
  event: NewEvent
): Promise<Event> => {
  const response = await axiosInstance.post(
    `/api/v1/calendars/${calendarId}/events`,
    event
  );
  return response.data;
};

const createReservation = async (
  calendarId: string,
  eventId: string
): Promise<Reservation> => {
  const response = await axiosInstance.post(
    `/api/v1/calendars/${calendarId}/events/${eventId}/reservations`
  );
  return response.data;
};

const deleteCalendar = async (calendarId: string): Promise<Calendar> => {
  const response = await axiosInstance.delete(
    `/api/v1/calendars/${calendarId}`
  );
  return response.data;
};

const getAll = async (params: URLSearchParams): Promise<Page<Calendar>> => {
  import.meta.env.DEBUG && console.log(params.get("name"));
  const response = await axiosInstance.get("/api/v1/calendars", { params });
  console.log("fetching calendars");
  return response.data;
};

export default {
  create,
  getAll,
  update,
  getCalendarWithEvents,
  createEvent,
  createReservation,
  deleteCalendar,
};
