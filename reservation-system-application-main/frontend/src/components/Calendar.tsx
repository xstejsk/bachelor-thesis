import React from "react";
import FullCalendar from "@fullcalendar/react"; // must go before plugins
import dayGridPlugin from "@fullcalendar/daygrid"; // a plugin!
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import { mapEventsToFullCalendarEvents } from "../EventUtils";
import csLocale from "@fullcalendar/core/locales/cs";
import CalendarHeader from "./CalendarHeader";
import { useParams } from "react-router";
import { useSearchParams } from "react-router-dom";
import calendarsApi from "../service/calendarsApi";
import EventCard from "./EventCard";
import { EventContentArg } from "@fullcalendar/core/index.js";
import { format } from "date-fns";
import { useUsersReservations } from "../hooks/useUsersReservations";
import CustomGridLoader from "./GridLoader";
import toast from "react-hot-toast";
import useStyleEvent from "../hooks/useStyleEvent";

const Calendar = () => {
  const calendarRef = React.useRef<FullCalendar>(null);
  const { calendarId } = useParams<{ calendarId: string }>();
  const [searchParams] = useSearchParams();
  const { styleEvent } = useStyleEvent();

  const { isCallable, isLoading } = useUsersReservations();

  const defaultView = window.innerWidth < 900 ? "timeGridDay" : "dayGridMonth";

  const handleResize = (event: UIEvent) => {
    const width = (event.currentTarget as Window).innerWidth || 0;
    if (calendarRef.current) {
      if (width < 900) {
        calendarRef.current.getApi().changeView("timeGridDay");
      } else {
        calendarRef.current.getApi().changeView("dayGridMonth");
      }
    }
  };

  const renderEventContent = (eventInfo: EventContentArg) => {
    return <EventCard eventInfo={eventInfo} />;
  };

  React.useEffect(() => {
    window.addEventListener("resize", handleResize);
    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, []);

  if (isCallable && isLoading) return <CustomGridLoader />;

  return (
    <>
      {calendarRef && <CalendarHeader calendarRef={calendarRef} />}

      <FullCalendar
        plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
        initialView={defaultView}
        weekends={true}
        events={function (fetchInfo, successCallback, failureCallback) {
          calendarsApi
            .getCalendarWithEvents(
              calendarId ?? "",
              format(fetchInfo.start, "yyyy-MM-dd"),
              format(fetchInfo.end, "yyyy-MM-dd")
            )
            .then((calendar) => {
              successCallback(mapEventsToFullCalendarEvents(calendar.events));
            })
            .catch((error) => {
              failureCallback(error);
              toast.error("Nepodařilo se načíst události.");
            });
        }}
        slotDuration={"00:15:00"}
        headerToolbar={false}
        locale={csLocale}
        eventOverlap={false}
        editable={false}
        ref={calendarRef}
        allDaySlot={false}
        height={"auto"}
        eventClassNames={styleEvent}
        eventContent={renderEventContent}
        slotMinWidth={50}
        slotMinTime={searchParams.get("minTime") ?? "00:00:00"}
        slotMaxTime={searchParams.get("maxTime") ?? "24:00:00"}
        loading={function (isLoading) {
          if (isLoading) {
            toast.loading("Načítání událostí...", {
              id: "loading",
            });
          } else {
            toast.dismiss("loading");
          }
        }}
      />
    </>
  );
};

export default Calendar;
