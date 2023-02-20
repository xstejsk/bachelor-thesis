import React, { useState, useRef, useContext, useEffect } from "react";
import { Context } from "../util/GlobalState";
import { Form, ButtonGroup } from "react-bootstrap";

const CalendarHeader = ({
  calendarRef,
  handleLocationChange,
  locationOptions,
  showAddEventForm,
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
    <>
      <div
        className="mb-3"
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          width: "100%",
        }}
      >
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
          class="btn-group"
          role="group"
          aria-label="Basic radio toggle button group"
          id="intervalViewOptions"
        >
          <input
            type="radio"
            class="btn-check"
            name="btnradio"
            id="btnradio1"
            autocomplete="off"
            onClick={dayHandle}
          />
          <label class="btn btn-outline-primary" for="btnradio1">
            Den
          </label>

          <input
            type="radio"
            class="btn-check"
            name="btnradio"
            id="btnradio2"
            autocomplete="off"
            onClick={weekHandle}
            defaultChecked
          />
          <label class="btn btn-outline-primary" for="btnradio2">
            Týden
          </label>

          <input
            type="radio"
            class="btn-check"
            name="btnradio"
            id="btnradio3"
            autocomplete="off"
            onClick={monthHandle}
          />
          <label class="btn btn-outline-primary" for="btnradio3">
            Měsíc
          </label>
        </div>
      </div>

      <div
        className="mb-3"
        style={{
          display: "flex",
          justifyContent: "start",
          alignItems: "center",
          width: "100%",
        }}
      >
        {globalState?.user?.role === "ROLE_ADMIN" && (
          <div
            class="btn-group"
            role="group"
            aria-label="Basic radio toggle button group"
            id="eventsViewOptions"
            style={{ marginRight: "20px" }}
          >
            <input
              type="radio"
              class="btn-check"
              name="viewOption"
              id="calendarButton"
              autocomplete="off"
              //onClick={dayHandle}
              defaultChecked
            />

            <label class="btn btn-outline-secondary" for="calendarButton">
              <i class="bi bi-calendar-fill"></i>
            </label>

            <input
              type="radio"
              class="btn-check"
              name="viewOption"
              id="tableButton"
              autocomplete="off"
              //onClick={weekHandle}
            />
            <label class="btn btn-outline-secondary" for="tableButton">
              <i class="bi bi-table"></i>
            </label>
          </div>
        )}

        <Form.Select
          id="locationSelect"
          style={{ width: "20%", marginRight: "20px" }}
          size="md"
          onChange={(e) => {
            handleLocationChange(e.target.value);
          }}
        >
          {locationOptions.map((locationOption) => (
            <option value={locationOption.value} key={locationOption.value}>
              {locationOption.label}
            </option>
          ))}
        </Form.Select>
        {globalState?.user?.role === "ROLE_ADMIN" && (
          <div className="fc-toolbar-chunk">
            <ButtonGroup id="eventsOperations">
              <button className="btn btn-primary" onClick={showAddEventForm}>
                Nová událost
              </button>
              <button className="btn btn-danger" onClick={showAddEventForm}>
                Smazat kalendář
              </button>
            </ButtonGroup>
          </div>
        )}
      </div>
    </>
  );
};
export default CalendarHeader;
