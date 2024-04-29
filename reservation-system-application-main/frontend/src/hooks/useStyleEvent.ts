import { EventContentArg } from "@fullcalendar/core";
import { EventExtendedProps } from "../models/calendar";
import { useRecoilValue } from "recoil";
import reservationsAtom from "../state/reservationsAtom";

const useStyleEvent = () => {
  const reservedEventsIds = useRecoilValue(reservationsAtom);

  const styleEvent = (eventInfo: EventContentArg) => {
    const { event } = eventInfo;
    const isReserved = reservedEventsIds.includes(event.id);
    if (isReserved) return "event-card--reserved";
    const extendedProps = event.extendedProps as EventExtendedProps;
    const isFull = extendedProps.spacesAvailable == 0;
    return isFull || (eventInfo.event.start ?? new Date()) < new Date()
      ? "event-card--full"
      : "event-card";
  };
  return { styleEvent };
};

export default useStyleEvent;
