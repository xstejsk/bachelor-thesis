import React, { useEffect, useState } from "react";
import {
  Inject,
  ScheduleComponent,
  Day,
  Week,
  WorkWeek,
  Month,
  Agenda,
  MonthAgenda,
  TimelineViews,
  TimelineMonth,
  EventSettingsModel,
  ViewDirective,
  ViewsDirective,
} from "@syncfusion/ej2-react-schedule";
import { host, mockEventsEndpoint } from "../util/EndpointConfig";
import addCategoryAndPriceFields from "../util/SchedulerEditorUtil";
import axios from "axios";

import CustomGridLoader from "./CustomGridLoader";

const CustomScheduler = () => {
  const [events, setEvents] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

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
          justifyContent: "center",
          alignItems: "center",
          display: "flex",
          padding: 5,
          background: "#FFC823",
        }}
      >
        <ScheduleComponent
          eventSettings={localData}
          popupOpen={onPopupOpen.bind(this)}
        >
          <ViewsDirective>
            <ViewDirective option="Day" />
            <ViewDirective option="Week" />
            <ViewDirective option="WorkWeek" />
            <ViewDirective option="Month" />
            <ViewDirective option="TimelineMonth" />
            <ViewDirective option="Agenda" />
          </ViewsDirective>

          <Inject
            services={[
              Day,
              Week,
              WorkWeek,
              Month,
              Agenda,
              MonthAgenda,
              TimelineViews,
              TimelineMonth,
            ]}
          />
        </ScheduleComponent>
      </div>
    );
  }
};

export default CustomScheduler;
