import React, { useState, useContext, useEffect } from "react";
import { Context } from "../util/GlobalState";
import { ButtonGroup } from "react-bootstrap";
import AdminCalendarHeader from "./AdminCalendarHeader";
import BasicCalendarHeader from "./BasicCalendarHeader";

const CalendarHeader = ({
  calendarRef,
  handleLocationChange,
  locationOptions,
  showAddEventForm,
  deleteCalendar,
  reloadLocations,
}) => {
  const [title, settitle] = useState(undefined);
  const [globalState, setGlobalState] = useContext(Context);

  useEffect(() => {
    todayHandle();
  }, []);
  const nextHandle = () => {
    calendarRef.current._calendarApi.next();
    settitle(
      calendarRef.current._calendarApi.currentDataManager.data.viewTitle
    );
  };

  const prevHandle = () => {
    calendarRef.current._calendarApi.prev();
    settitle(
      calendarRef.current._calendarApi.currentDataManager.data.viewTitle
    );
  };
  const todayHandle = () => {
    calendarRef.current._calendarApi.today();
    settitle(
      calendarRef.current._calendarApi.currentDataManager.data.viewTitle
    );
  };
  const dayHandle = () => {
    calendarRef.current._calendarApi.changeView("timeGridDay");
    settitle(
      calendarRef.current._calendarApi.currentDataManager.data.viewTitle
    );
  };
  const weekHandle = () => {
    calendarRef.current._calendarApi.changeView("timeGridWeek");
    settitle(
      calendarRef.current._calendarApi.currentDataManager.data.viewTitle
    );
  };
  const monthHandle = () => {
    calendarRef.current._calendarApi.changeView("dayGridMonth");
    settitle(
      calendarRef.current._calendarApi.currentDataManager.data.viewTitle
    );
  };

  return (
    <div>
      <div className="calendarViews">
        <ButtonGroup id="daysButtonGroup" style={{ marginRight: "20px" }}>
          <button className="btn btn-primary" onClick={() => prevHandle()}>
            &#10094;
          </button>
          <button className="btn btn-primary" onClick={() => todayHandle()}>
            Dnes
          </button>
          <button className="btn btn-primary" onClick={() => nextHandle()}>
            &#10095;
          </button>
        </ButtonGroup>

        <div
          id="calendarTitle"
          style={{ display: "flex", justifyContent: "center" }}
        >
          <h3 style={{ align: "center" }} id="title">
            {title}
          </h3>
        </div>

        <div
          className="btn-group"
          role="group"
          aria-label="Basic radio toggle button group"
          id="intervalViewOptions"
        >
          <input
            type="radio"
            className="btn-check"
            name="btnradio"
            id="btnradio1"
            autoComplete="off"
            onClick={dayHandle}
          />
          <label className="btn btn-outline-primary" htmlFor="btnradio1">
            Den
          </label>

          <input
            type="radio"
            className="btn-check"
            name="btnradio"
            id="btnradio2"
            autoComplete="off"
            onClick={weekHandle}
            defaultChecked
          />
          <label className="btn btn-outline-primary" htmlFor="btnradio2">
            Týden
          </label>

          <input
            type="radio"
            className="btn-check"
            name="btnradio"
            id="btnradio3"
            autoComplete="off"
            onClick={monthHandle}
          />
          <label className="btn btn-outline-primary" htmlFor="btnradio3">
            Měsíc
          </label>
        </div>
      </div>
      {globalState?.user?.role === "ROLE_ADMIN" ||
      globalState?.user?.role === "ROLE_SUPER_ADMIN" ? (
        <AdminCalendarHeader
          handleLocationChange={handleLocationChange}
          showAddEventForm={showAddEventForm}
          reloadLocations={reloadLocations}
          deleteCalendar={deleteCalendar}
          locationOptions={locationOptions}
        />
      ) : (
        <BasicCalendarHeader
          locationOptions={locationOptions}
          handleLocationChange={handleLocationChange}
        />
      )}
    </div>
  );
};
export default CalendarHeader;
