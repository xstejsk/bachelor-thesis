import { User } from "./user";

export type NewCalendar = {
  name: string;
  thumbnail: string;
  locationId: string;
};

export type UpdateCalendarRequest = NewCalendar & {
  id: string;
};

export type Calendar = NewCalendar & {
  id: string;
  location: Location;
  minTime: string;
  maxTime: string;
};

export type Location = {
  id: string;
  name: string;
};

export type CalendarWithEvents = Calendar & {
  events: Event[];
};

export type Recurrence =
  | undefined
  | null
  | {
      repeatUntil: string;
      daysOfWeek: number[];
    };

export type EventExtendedProps = {
  recurrence: Recurrence;
  maximumCapacity: number;
  spacesAvailable: number;
  price: number;
  discountPrice: number;
  description: string;
};

type EventBase = {
  title: string;
  description: string;
  date: string;
  startTime: string;
  endTime: string;
  price: number;
  discountPrice: number;
  maximumCapacity: number;
  recurrence: Recurrence;
};

export type Event = EventBase & {
  id: string;
  spacesAvailable: number;
};

export type EventWithReservations = Event & {
  reservations: Reservation[];
};

export type NewEvent = EventBase;

export type EditEvent = {
  title: string;
  description: string;
  updateSeries: boolean;
};

export type Reservation = {
  id: string;
  owner: User;
  event: Event;
  discountApplied: boolean;
};
