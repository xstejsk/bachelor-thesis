import React, { useEffect, useState } from "react";
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

import CustomGridLoader from "./CustomLoader";

const CustomScheduler = () => {
  const [events, setEvents] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [showAddEventForm, setShowAddEventForm] = useState(false);

  const onPopupOpen = (args) => {
    addCategoryAndPriceFields(args);
  };

  useEffect(() => {
    axios
      .get(host + mockEventsEndpoint, { timeout: 10000 })
      .then((response) => {
        setEvents(response.data);
        console.log(response.data);
      })
      .catch((err) => {
        console.log(err);
        setEvents([]);
        // handle timeout
      })
      .finally(() => setIsLoading(false));
  }, []);
  const localData = {
    dataSource: events,
    fields: {
      subject: { name: "subject" },
      id: { name: "id" },
      description: { name: "description" },
      startTime: { name: "startTime" },
      endTime: { name: "endTime" },
      capacity: { name: "capacity" },
      endTime: { name: "endTime" },
      isAllDay: { name: "isAllDay" },
      recurrenceRule: { name: "recurrenceRule" },
    },
  };
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
        ></NewEventForm>
      </div>
    );
  }
};

export default CustomScheduler;
