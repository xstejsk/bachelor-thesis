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
  opensAt,
  closesAt,
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
    } else if (
      globalState?.user?.role === "ROLE_ADMIN" ||
      globalState?.user?.role === "ROLE_SUPER_ADMIN"
    ) {
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
        <div className="event-card monthly">
          <h6>
            {title}
            {(globalState?.user?.role === "ROLE_ADMIN" ||
              globalState?.user?.role === "ROLE_SUPER_ADMIN") && (
              <span style={{ marginLeft: "5px" }}>
                [ID:{eventInfo.event.id}]
              </span>
            )}
          </h6>
          <div>
            {maximumCapacity - availableCapacity}/{maximumCapacity}
          </div>
        </div>
      );
    } else {
      return (
        <div className="event-card">
          <h5>{title}</h5>

          {(globalState?.user?.role === "ROLE_ADMIN" ||
            globalState?.user?.role === "ROLE_SUPER_ADMIN") && (
            <span>[ID:{eventInfo.event.id}]</span>
          )}

          <div>
            {startTime} - {endTime}
          </div>

          <div className="event-details">
            <div>Cena: {price}Kč</div>
            <div>
              Obsazení: {maximumCapacity - availableCapacity}/{maximumCapacity}
            </div>
          </div>
          <div>{description}</div>
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
          slotMinTime={opensAt}
          headerToolbar={false}
          eventContent={renderEventContent}
          slotMaxTime={closesAt}
          slotDuration={"00:15:00"}
          eventColor={"#0082ec"}
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
