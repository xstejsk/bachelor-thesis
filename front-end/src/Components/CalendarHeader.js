import React, { useState, useRef, useContext, useEffect } from "react";
import { Context } from "../util/GlobalState";
import { Form, ButtonGroup } from "react-bootstrap";
import Button from "react-bootstrap/Button";
import { Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";
import NewLocationForm from "./Forms/NewLocationForm";

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
  const [openDeleteCalendarModal, setOpenDeleteCalendarModal] = useState(false);
  const [openLocationFrom, setOpenLocationForm] = useState(false);

  useEffect(() => {
    todayHandle();
  }, []);
  const nextHandle = () => {
    calendarRef.current._calendarApi.next();
    settitle(
      calendarRef.current._calendarApi.currentDataManager.data.viewTitle
    );
  };

  const handleDeleteCalendar = () => {
    deleteCalendar();
    hideDeleteCalendarModal();
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

  const hideDeleteCalendarModal = () => {
    setOpenDeleteCalendarModal(false);
  };

  const showDeleteCalendarModal = () => {
    setOpenDeleteCalendarModal(true);
  };

  useEffect(() => {
    console.log("location option size");
    console.log(locationOptions.length);
  }, []);

  function handleHideNewLocationModal() {
    console.log("handle hide new location modal");
    setOpenLocationForm(false);
  }

  function showAddLocationFrom() {
    setOpenLocationForm(true);
  }

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
            className="btn-group"
            role="group"
            aria-label="Basic radio toggle button group"
            id="eventsViewOptions"
            style={{ marginRight: "20px" }}
          >
            <input
              type="radio"
              className="btn-check"
              name="viewOption"
              id="calendarButton"
              autoComplete="off"
              //onClick={dayHandle}
              defaultChecked
            />

            <label
              className="btn btn-outline-secondary"
              htmlFor="calendarButton"
            >
              <i className="bi bi-calendar-fill"></i>
            </label>

            <input
              type="radio"
              className="btn-check"
              name="viewOption"
              id="tableButton"
              autoComplete="off"
              //onClick={weekHandle}
            />
            <label className="btn btn-outline-secondary" htmlFor="tableButton">
              <i className="bi bi-table"></i>
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
            <button
              className="btn btn-primary"
              onClick={showAddEventForm}
              style={{ marginRight: "20px" }}
            >
              Nová událost
            </button>
            <ButtonGroup id="eventsOperations">
              <button className="btn btn-primary" onClick={showAddLocationFrom}>
                Nový kalendář
              </button>
              <button
                className="btn btn-danger"
                onClick={showDeleteCalendarModal}
                disabled={locationOptions.length < 2}
              >
                Smazat kalendář
              </button>
            </ButtonGroup>
          </div>
        )}

        <Modal
          isOpen={openDeleteCalendarModal}
          on
          backdrop="static"
          size="sm"
          centered={true}
        >
          <ModalHeader>Smazat kalendář</ModalHeader>
          <ModalBody>
            {
              "Smazáním kalendáře dojde ke zrušení veškerých příslušných událostí, opravdu si přejete pokračovat?"
            }
          </ModalBody>
          <ModalFooter>
            <Button variant="secondary" onClick={hideDeleteCalendarModal}>
              Ne
            </Button>
            <Button variant="danger" onClick={handleDeleteCalendar}>
              Ano
            </Button>
          </ModalFooter>
        </Modal>

        <NewLocationForm
          handleHide={handleHideNewLocationModal}
          isOpen={openLocationFrom}
          reloadLocations={reloadLocations}
        />
      </div>
    </>
  );
};
export default CalendarHeader;
