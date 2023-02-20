import React, { useState, useRef, useContext, useEffect } from "react";
import FullCalendar from "@fullcalendar/react"; // must go before plugins
import dayGridPlugin from "@fullcalendar/daygrid"; // a plugin!
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import NewEventForm from "./Forms/NewEventForm";
import { Context } from "../util/GlobalState";
import { Row } from "reactstrap";
import csLocale from "@fullcalendar/core/locales/cs";
import SignUpModal from "./Forms/SignUpToEventModal";
import DetailsModal from "./Forms/DetailsModal";
import NewLocationForm from "./Forms/NewLocationForm";
import CalendarHeader from "./CalendarHeader";

const CustomScheduler = ({
  events,
  currentLocationId,
  reloadLocations,
  reloadEvents,
  handleLocationChange,
  locationOptions,
}) => {
  const [showAddEventForm, setShowAddEventForm] = useState(false);
  const [showSignUpToEvent, setShowSignUpToEvent] = useState(false);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [showAddLocationFrom, setShowAddLocationForm] = useState(false);
  const calendarRef = useRef(null);
  const [state, setState] = useState({ clickInfo: null });
  const [globalState, setGlobalState] = useContext(Context);

  const handleEventClick = (clickInfo) => {
    if (globalState?.user?.role === "ROLE_USER") {
      setState({ clickInfo: clickInfo });
      // console.log("user");
      setShowSignUpToEvent(true);
    } else if (globalState?.user?.role === "ROLE_ADMIN") {
      setState({ clickInfo: clickInfo });
      setShowDetailsModal(true);
    }
  };

  function renderEventContent(eventInfo) {
    console.log(eventInfo);
    const { availableCapacity, maximumCapacity } =
      eventInfo.event.extendedProps;
    const title = eventInfo.event.title;
    const startTime = new Date(eventInfo.event.startStr).toLocaleTimeString(
      [],
      { hour: "2-digit", minute: "2-digit" }
    );
    const endTime = new Date(eventInfo.event.endStr).toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });
    return (
      <>
        <div style={{ fontWeight: "bold" }}>
          {startTime} - {endTime}
        </div>

        <div>{title}</div>
        {globalState?.user?.role === "ROLE_ADMIN" && (
          <div>ID:{eventInfo.event.id}</div>
        )}
        <div style={{ fontSize: "smaller", marginTop: "5px" }}>
          Obsazeno míst: {maximumCapacity - availableCapacity}/{maximumCapacity}
        </div>
      </>
    );
  }

  function handleHideSignUpModal() {
    console.log("handle hide sign up");
    setShowSignUpToEvent(false);
  }

  function handleHideDetailsModal() {
    console.log("handle hide details");
    setShowDetailsModal(false);
  }

  function handleHideNewEventModal() {
    console.log("handle hide new event");
    setShowAddEventForm(false);
  }

  function handleHideNewLocationModal() {
    console.log("handle hide new location modal");
    setShowAddLocationForm(false);
  }

  return (
    <div
      style={{
        //justifyContent: "center",
        //alignItems: "center",
        //display: "flex",
        paddingTop: 3,
        paddingLeft: 10,
        paddingRight: 10,
        //height: "100%",
        // background: "#c93060",
      }}
    >
      <Row>
        {calendarRef && (
          <CalendarHeader
            calendarRef={calendarRef}
            handleLocationChange={handleLocationChange}
            locationOptions={locationOptions}
            showAddEventForm={() => setShowAddEventForm(true)}
          />
        )}

        <FullCalendar
          events={events}
          // businessHours={{
          //   // days of week. an array of zero-based day of week integers (0=Sunday)
          //   daysOfWeek: [1, 2, 3, 4], // Monday - Thursday

          //   startTime: "10:00", // a start time (10am in this example)
          //   endTime: "18:00", // an end time (6pm in this example)
          // }}
          // eventMouseEnter={handleMouseEnter}
          eventClick={handleEventClick}
          editable={false}
          slotMinTime={"5:00"}
          headerToolbar={false}
          eventContent={renderEventContent}
          // eventStartEditable={false}
          slotMaxTime={"22:00"}
          slotDuration={"00:15:00"}
          locale={csLocale}
          ref={calendarRef}
          height={"auto"}
          plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
          customButtons={
            globalState?.user?.role === "ROLE_ADMIN" && {
              addEvent: {
                text: "Nová událost",
                click: () => setShowAddEventForm(true),
              },
              addLocation: {
                text: "Nové místo konání",
                click: () => setShowAddLocationForm(true),
              },
            }
          }
          initialView="timeGridWeek"
          views={["dayGridMonth", "dayGridWeek", "dayGridDay"]}
          themeSystem="bootstrap5"
          eventOverlap={false}
        />
      </Row>

      {state.clickInfo?.event && showDetailsModal && (
        <DetailsModal
          locationId={currentLocationId}
          eventObj={state.clickInfo.event}
          handleHide={handleHideDetailsModal}
          isOpen={showDetailsModal}
          reloadEvents={reloadEvents}
        />
      )}
      {showAddEventForm && (
        <NewEventForm
          locationId={currentLocationId}
          reloadEvents={reloadEvents}
          handleHide={handleHideNewEventModal}
          isOpen={showAddEventForm}
        />
      )}

      <NewLocationForm
        handleHide={handleHideNewLocationModal}
        isOpen={showAddLocationFrom}
        reloadLocations={reloadLocations}
      />

      {state.clickInfo && (
        <SignUpModal
          clickInfo={state.clickInfo}
          handleHide={handleHideSignUpModal}
          isOpen={showSignUpToEvent}
          reloadEvents={reloadEvents}
        />
      )}
    </div>
  );
};
export default CustomScheduler;
