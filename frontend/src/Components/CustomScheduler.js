import React, { useState, useRef, useContext } from "react";
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
import CalendarHeader from "./CalendarHeader";

const CustomScheduler = ({
  events,
  currentLocationId,
  reloadLocations,
  reloadEvents,
  handleLocationChange,
  locationOptions,
  deleteCalendar,
}) => {
  const [showAddEventForm, setShowAddEventForm] = useState(false);
  const [showSignUpToEvent, setShowSignUpToEvent] = useState(false);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const calendarRef = useRef(null);
  const [state, setState] = useState({ clickInfo: null });
  const [globalState, setGlobalState] = useContext(Context);

  const handleEventClick = (clickInfo) => {
    if (globalState?.user?.role === "ROLE_USER") {
      setState({ clickInfo: clickInfo });
      setShowSignUpToEvent(true);
    } else if (globalState?.user?.role === "ROLE_ADMIN") {
      setState({ clickInfo: clickInfo });
      setShowDetailsModal(true);
    }
  };

  function renderEventContent(eventInfo) {
    console.log(eventInfo);
    const { availableCapacity, maximumCapacity, price, description } =
      eventInfo.event.extendedProps;
    const title = eventInfo.event.title;
    const view = eventInfo.view;
    const startTime = new Date(eventInfo.event.startStr).toLocaleTimeString(
      [],
      { hour: "2-digit", minute: "2-digit" }
    );
    const endTime = new Date(eventInfo.event.endStr).toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });
    if (view.type === "dayGridMonth") {
      return (
        <div
          style={{
            fontWeight: "bold",

            display: "flex",
            justifyContent: "space-between",
          }}
        >
          {/* left side */}
          <div style={{ fontWeight: "bold" }}>
            {title}
            {globalState?.user?.role === "ROLE_ADMIN" && (
              <span style={{ marginLeft: "5px" }}>
                [ID:{eventInfo.event.id}]
              </span>
            )}
          </div>
          {/* right side  */}
          <div style={{ fontSize: "smaller" }}>
            {maximumCapacity - availableCapacity}/{maximumCapacity}
          </div>
        </div>
      );
    } else {
      return (
        <div>
          <div style={{ fontWeight: "bold" }}>
            {title}
            {globalState?.user?.role === "ROLE_ADMIN" && (
              <span style={{ marginLeft: "5px" }}>
                [ID:{eventInfo.event.id}]
              </span>
            )}
          </div>

          <div
            style={{
              fontSize: "smaller",
            }}
          >
            {startTime} - {endTime}
          </div>

          <div
            style={{
              fontSize: "smaller",
            }}
          >
            {description}
          </div>
          <div style={{ position: "absolute", bottom: 0, width: "100%" }}>
            <div
              style={{
                fontSize: "smaller",
                marginTop: "5px",
                display: "flex",
                justifyContent: "space-between",
              }}
            >
              <div>Cena: {price}Kč</div>
              <div style={{ fontWeight: "bold" }}>
                Obsazení: {maximumCapacity - availableCapacity}/
                {maximumCapacity}
              </div>
            </div>
          </div>
        </div>
      );
    }
  }

  function handleHideSignUpModal() {
    setShowSignUpToEvent(false);
  }

  function handleHideDetailsModal() {
    setShowDetailsModal(false);
  }

  function handleHideNewEventModal() {
    setShowAddEventForm(false);
  }

  function handleDeleteCalendar() {
    deleteCalendar();
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
            deleteCalendar={handleDeleteCalendar}
            reloadLocations={reloadLocations}
          />
        )}

        <FullCalendar
          events={events}
          eventClick={handleEventClick}
          editable={false}
          slotMinTime={"5:00"}
          headerToolbar={false}
          eventContent={renderEventContent}
          slotMaxTime={"22:00"}
          slotDuration={"00:15:00"}
          locale={csLocale}
          ref={calendarRef}
          height={"auto"}
          allDaySlot={false}
          plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
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
      {/* {showAddEventForm && (
        <NewEventForm
          locationId={currentLocationId}
          reloadEvents={reloadEvents}
          handleHide={handleHideNewEventModal}
          isOpen={showAddEventForm}
        />
      )} */}

      <NewEventForm
        locationId={currentLocationId}
        reloadEvents={reloadEvents}
        handleHide={handleHideNewEventModal}
        isOpen={showAddEventForm}
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
