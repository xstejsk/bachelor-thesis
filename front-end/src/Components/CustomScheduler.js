import React, { useState, useRef, useContext, useEffect } from "react";
import FullCalendar from "@fullcalendar/react"; // must go before plugins
import dayGridPlugin from "@fullcalendar/daygrid"; // a plugin!
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import NewEventForm from "./Forms/NewEventForm";
import { Context } from "../util/GlobalState";
import { Row, Col, Button } from "reactstrap";
import csLocale from "@fullcalendar/core/locales/cs";
import SignUpModal from "./Forms/SignUpToEventModal";
import DetailsModal from "./DetailsModal";
import NewLocationForm from "./Forms/NewLocationForm";

const CustomScheduler = ({
  allEvents,
  currentLocationId,
  addEvents,
  removeEvent,
  removeEventGroup,
  addLocation,
}) => {
  const [showAddEventForm, setShowAddEventForm] = useState(false);
  const [showSignUpToEvent, setShowSignUpToEvent] = useState(false);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [showAddLocationFrom, setShowAddLocationForm] = useState(false);
  const calendarRef = useRef(null);
  const [state, setState] = useState({ clickInfo: null });
  const [globalState, setGlobalState] = useContext(Context);
  const [eventsByLocation, setEventsByLocation] = useState(null);

  const filterEventsByLocation = () => {
    setEventsByLocation(
      allEvents.filter((event) => event.locationId === currentLocationId)
    );
    // console.log("events in current location -------");
    // console.log(
    //   allEvents.filter((event) => event.locationId === currentLocationId)
    // );
  };

  useEffect(() => {
    // console.log("filtering events by locations");
    filterEventsByLocation();
  }, [currentLocationId, allEvents]);

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
      {/* <Row style={{ marginBottom: 20 }}>
        <Col
          sm={{ size: 3, offset: 6 }}
          md={{ size: 3, offset: 6 }}
          style={{
            paddingRight: 15,
          }}
        >
          <Button
            style={{ float: "right" }}
            color="primary"
            onClick={() => setShowAddEventForm(true)}
          >
            Nová událost
          </Button>
        </Col>
      </Row> */}
      <Row>
        <FullCalendar
          events={eventsByLocation}
          // businessHours={{
          //   // days of week. an array of zero-based day of week integers (0=Sunday)
          //   daysOfWeek: [1, 2, 3, 4], // Monday - Thursday

          //   startTime: "10:00", // a start time (10am in this example)
          //   endTime: "18:00", // an end time (6pm in this example)
          // }}
          // eventMouseEnter={handleMouseEnter}
          eventClick={handleEventClick}
          editable={true}
          slotMinTime={"5:00"}
          // eventStartEditable={false}
          slotMaxTime={"22:00"}
          slotDuration={"00:30:00"}
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
          headerToolbar={
            globalState?.user?.role === "ROLE_ADMIN"
              ? {
                  left: "today,prev,next addEvent addLocation",
                  center: "title",
                  right: "timeGridDay,timeGridWeek,dayGridMonth",
                }
              : {
                  left: "today,prev,next",
                  center: "title",
                  right: "timeGridDay,timeGridWeek,dayGridMonth",
                }
          }
          initialView="timeGridWeek"
          views={["dayGridMonth", "dayGridWeek", "dayGridDay"]}
          themeSystem="bootstrap5"
          eventOverlap={false}
        />
      </Row>

      {state.clickInfo?.event && (
        <DetailsModal
          locationId={currentLocationId}
          addEvents={addEvents}
          event={state.clickInfo.event}
          handleHide={handleHideDetailsModal}
          isOpen={showDetailsModal}
          removeEvent={removeEvent}
          removeEventGroup={removeEventGroup}
        />
      )}

      <NewEventForm
        locationId={currentLocationId}
        addEvents={addEvents}
        handleHide={handleHideNewEventModal}
        isOpen={showAddEventForm}
      />

      <NewLocationForm
        handleHide={handleHideNewLocationModal}
        isOpen={showAddLocationFrom}
        addLocation={addLocation}
      />

      {state.clickInfo && (
        <SignUpModal
          clickInfo={state.clickInfo}
          handleHide={handleHideSignUpModal}
          isOpen={showSignUpToEvent}
        />
      )}
    </div>
  );
};
export default CustomScheduler;
