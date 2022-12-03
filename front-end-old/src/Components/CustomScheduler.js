import React, { useEffect, useState, useRef } from "react";
import FullCalendar from "@fullcalendar/react"; // must go before plugins
import dayGridPlugin from "@fullcalendar/daygrid"; // a plugin!
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import { host, mockEventsEndpoint } from "../util/EndpointConfig";
import addCategoryAndPriceFields from "../util/SchedulerEditorUtil";
import Select from "react-select";
import axios from "axios";
import NewEventForm from "./Forms/NewEventForm";
import {
  Row,
  Col,
  Button,
  FormGroup,
  Label,
  Input,
  Container,
} from "reactstrap";
import DaysSelector from "./DaysSelector";
import csLocale from "@fullcalendar/core/locales/cs";
import CustomGridLoader from "./CustomLoader";
import SignUpModal from "./Forms/SignUpToEventModal";
const CustomScheduler = () => {
  const [events, setEvents] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [showAddEventForm, setShowAddEventForm] = useState(false);
  const [showSignUpToEvent, setShowSignUpToEvent] = useState(false);
  const calendarRef = useRef(null);
  const [state, setState] = useState({});

  const onPopupOpen = (args) => {
    addCategoryAndPriceFields(args);
  };

  const handleEventClick = (clickInfo) => {
    setState({ clickInfo: clickInfo });
    console.log(state);
    setShowSignUpToEvent(true);
  };

  useEffect(() => {
    console.log("useEffect hit");
    axios
      .get(host + mockEventsEndpoint, { timeout: 10000 })
      .then((response) => {
        if (response.status === 200) {
          setEvents(response.data);
          console.log(response.data);
        }
      })
      .catch((err) => {
        console.log(err);
        setEvents([]);
        // handle timeout
      })
      .finally(() => setIsLoading(false));
  }, []);

  if (isLoading) {
    return <CustomGridLoader isLoading={isLoading} />;
  } else {
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
        <Row style={{ marginBottom: 20 }}>
          <Col
            sm={{ size: 3 }}
            md={{ size: 3 }}
            style={{
              paddingLeft: 15,
            }}
          >
            <Select
            // style={{ float: "left" }}
            // defaultValue={departments[0]}
            // options={departments}
            // onChange={(element) => onFilter(element)}
            />
          </Col>
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
        </Row>
        <Row>
          <FullCalendar
            initialEvents={events}
            // businessHours={{
            //   // days of week. an array of zero-based day of week integers (0=Sunday)
            //   daysOfWeek: [1, 2, 3, 4], // Monday - Thursday

            //   startTime: "10:00", // a start time (10am in this example)
            //   endTime: "18:00", // an end time (6pm in this example)
            // }}
            eventClick={handleEventClick}
            editable={true}
            slotMinTime={"8:00"}
            slotMaxTime={"18:00"}
            slotDuration={"00:30:00"}
            locale={csLocale}
            ref={calendarRef}
            height={"auto"}
            plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
            headerToolbar={{
              left: "today prev next",
              right: "timeGridDay timeGridWeek dayGridMonth",
            }}
            initialView="timeGridWeek"
            views={["dayGridMonth", "dayGridWeek", "dayGridDay"]}
            themeSystem="bootstrap5"
          />
        </Row>
        <NewEventForm
          isOpen={showAddEventForm}
          setIsOpen={setShowAddEventForm}
          calendarRef={calendarRef}
        />
        <SignUpModal
          setIsOpen={setShowSignUpToEvent}
          isOpen={showSignUpToEvent}
          clickInfo={state.clickInfo}
        />
      </div>
    );
  }
};

export default CustomScheduler;
