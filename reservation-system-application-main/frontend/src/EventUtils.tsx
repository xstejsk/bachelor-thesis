import { EventInput } from "@fullcalendar/core";
import { Event } from "./models/calendar";
import { parseISO } from "date-fns";

export type ExtendedProps = {
  recurrence: Event;
};

export const mapEventsToFullCalendarEvents = (
  events: Event[]
): EventInput[] => {
  return events.map((event) => ({
    id: event.id,
    title: event.title,
    start: parseISO(event.date + "T" + event.startTime).toISOString(),
    end: parseISO(event.date + "T" + event.endTime).toISOString(),
    overlap: false,
    editable: false,
    allDay: false,
    interactive: false,
    extendedProps: {
      recurrence: event.recurrence,
      maximumCapacity: event.maximumCapacity,
      spacesAvailable: event.spacesAvailable,
      price: event.price,
      discountPrice: event.discountPrice,
      description: event.description,
    },
  }));
};
