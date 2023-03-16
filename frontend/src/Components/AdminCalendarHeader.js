import React, { useState } from "react";
import { Form, ButtonGroup } from "react-bootstrap";
import Button from "react-bootstrap/Button";
import { Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";
import NewLocationForm from "./Forms/NewLocationForm";

const AdminCalendarHeader = ({
  handleLocationChange,
  showAddEventForm,
  reloadLocations,
  deleteCalendar,
  locationOptions,
}) => {
  const [openDeleteCalendarModal, setOpenDeleteCalendarModal] = useState(false);
  const [openLocationFrom, setOpenLocationForm] = useState(false);

  function handleHideNewLocationModal() {
    console.log("handle hide new location modal");
    setOpenLocationForm(false);
  }

  function showAddLocationFrom() {
    setOpenLocationForm(true);
  }

  const showDeleteCalendarModal = () => {
    setOpenDeleteCalendarModal(true);
  };
  const hideDeleteCalendarModal = () => {
    setOpenDeleteCalendarModal(false);
  };
  const handleDeleteCalendar = () => {
    deleteCalendar();
    hideDeleteCalendarModal();
  };

  return (
    <div>
      <div className="adminCalendarToolbar">
        <div className="calendarOperations">
          <div
            className="btn-group"
            role="group"
            aria-label="Basic radio toggle button group"
            id="eventsViewOptions"
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
          <Form.Select
            id="locationSelect"
            size="md"
            onChange={(e) => {
              console.log("chnanged location");
              console.log(e.target.value);
              handleLocationChange(e.target.value);
            }}
          >
            {locationOptions.map((locationOption) => (
              <option value={locationOption.value} key={locationOption.value}>
                {locationOption.label}
              </option>
            ))}
          </Form.Select>
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

        <button className="btn btn-primary" onClick={showAddEventForm}>
          Nová událost
        </button>
      </div>

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
  );
};

export default AdminCalendarHeader;
