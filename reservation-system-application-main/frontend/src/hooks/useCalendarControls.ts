import React from "react";
import FullCalendar from "@fullcalendar/react";
import { useRecoilValue } from "recoil";
import reservationsAtom from "../state/reservationsAtom";
import { ReservationModalProps } from "../components/CalendarHeader";

type CalendarControlsProps = {
  calendarRef: React.RefObject<FullCalendar>;
  setReservationModal: (props: ReservationModalProps) => void;
  setViewIndex: (index: number) => void;
};

export const useCalendarControls = (props: CalendarControlsProps) => {
  const reservedEventsIds = useRecoilValue(reservationsAtom);
  const [title, settitle] = React.useState<string>(
    props.calendarRef.current
      ? props.calendarRef.current.getApi().view.title
      : ""
  );
  const [date, setDate] = React.useState<string>(
    props.calendarRef.current
      ? props.calendarRef.current.getApi().view.title
      : ""
  );

  const nextHandle = () => {
    props.calendarRef.current
      ? props.calendarRef.current.getApi().next()
      : null;

    settitle(
      props.calendarRef.current
        ? props.calendarRef.current.getApi().view.title
        : ""
    );
    setDate("");
  };

  const prevHandle = () => {
    props.calendarRef.current
      ? props.calendarRef.current.getApi().prev()
      : null;

    settitle(
      props.calendarRef.current
        ? props.calendarRef.current.getApi().view.title
        : ""
    );
    setDate("");
  };
  const todayHandle = () => {
    props.calendarRef.current
      ? props.calendarRef.current.getApi().today()
      : null;
    settitle(
      props.calendarRef.current
        ? props.calendarRef.current.getApi().view.title
        : ""
    );
  };

  const dayHandle = () => {
    props.calendarRef.current
      ? props.calendarRef.current.getApi().changeView("timeGridDay")
      : null;
    todayHandle();
    settitle(
      props.calendarRef.current
        ? props.calendarRef.current.getApi().view.title
        : ""
    );
  };
  const weekHandle = () => {
    props.calendarRef.current
      ? props.calendarRef.current.getApi().changeView("timeGridWeek")
      : null;
    todayHandle();
    settitle(
      props.calendarRef.current
        ? props.calendarRef.current.getApi().view.title
        : ""
    );
  };
  const monthHandle = () => {
    props.calendarRef.current
      ? props.calendarRef.current.getApi().changeView("dayGridMonth")
      : null;
    todayHandle();
    settitle(
      props.calendarRef.current
        ? props.calendarRef.current.getApi().view.title
        : ""
    );
  };

  const gotoDateHandle = (date: string) => {
    props.calendarRef.current
      ? props.calendarRef.current.getApi().gotoDate(date)
      : null;
    settitle(
      props.calendarRef.current
        ? props.calendarRef.current.getApi().view.title
        : ""
    );
    setDate(date);
  };

  const selectView = (index: number) => () => {
    if (typeof index === "number") {
      switch (index) {
        case 0:
          dayHandle();
          break;
        case 1:
          weekHandle();
          break;
        case 2:
          monthHandle();
          break;
        default:
          break;
      }
      props.setViewIndex(index);
    }
  };

  props.calendarRef.current?.getApi().on("eventClick", (info) => {
    if (reservedEventsIds.includes(info.event.id)) {
      return;
    } else if (
      info.event.start! > new Date() &&
      info.event.extendedProps?.spacesAvailable > 0
    ) {
      props.setReservationModal({
        eventImpl: info.event,
        modalOpen: true,
      });
    }
  });

  return {
    nextHandle,
    prevHandle,
    todayHandle,
    gotoDateHandle,
    selectView,
    title,
    date,
  };
};
