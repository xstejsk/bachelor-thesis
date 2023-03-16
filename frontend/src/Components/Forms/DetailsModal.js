import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-daterangepicker/daterangepicker.css";
import React, { useEffect, useState } from "react";
import {
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Button,
  FormGroup,
  Label,
  Input,
  FormFeedback,
  Col,
  Row,
} from "reactstrap";
import { Form, InputGroup } from "react-bootstrap";
import Select from "react-select";
import axios from "axios";
import DaysSelector from "../DaysSelector";
import {
  host,
  locationsEndpoint,
  cancelEventById,
  cancelEventsByGroupId,
  updateEventEndpoint,
  updateRecurrenceGroup,
} from "../../util/EndpointConfig";
import { useAlert } from "react-alert";

const DetailsModal = ({
  handleHide,
  isOpen,
  locationId,
  eventObj,
  reloadEvents,
}) => {
  const [readOnly, setReadOnly] = useState(true);
  const recurrenceOptions = [
    { value: "NEVER", label: "Nikdy" },
    { value: "WEEKLY", label: "Týdně" },
    { value: "MONTHLY", label: "Měsíčně" },
  ];
  const [formHasErrors, setFormHasErrors] = useState(false);
  const [locationsOptions, setLocationsOptions] = useState([]);
  const [showFrequencyOptions, setShowFrequencyOptions] = useState(false);
  const [showCancelConfirmationModal, setShowCancelConfirmationModal] =
    useState(false);
  const [showCancelOptions, setShowCancelOptions] = useState(false);
  const [showDefaultDetailsModal, setShowDefaultDetailsModal] = useState(true);
  const [eventsOverlap, setEventsOverlap] = useState(false);

  function viewOnlyCancelOptionsModal() {
    setShowCancelOptions(true);
    setShowCancelConfirmationModal(false);
    setShowFrequencyOptions(false);
    setShowDefaultDetailsModal(false);
  }

  function viewOnlyCancelConfirmationModal() {
    setShowCancelConfirmationModal(true);
    setShowCancelOptions(false);
    setShowFrequencyOptions(false);
    setShowDefaultDetailsModal(false);
  }

  function viewOnlyFrequencyOptionsModal() {
    setShowFrequencyOptions(true);
    setShowCancelConfirmationModal(false);
    setShowCancelOptions(false);
    setShowDefaultDetailsModal(false);
  }

  function viewOnlyDefaultDetailsModal() {
    setShowDefaultDetailsModal(true);
    setShowFrequencyOptions(false);
    setShowCancelConfirmationModal(false);
    setShowCancelOptions(false);
  }

  const [changeEventRequest, setChangeEventRequest] = useState({
    id: eventObj.id,
    title: eventObj.title,
    price: eventObj.extendedProps.price,
    maximumCapacity: eventObj.extendedProps.maximumCapacity,
    description: eventObj.extendedProps.description,
    locationId: locationId,
    recurrenceGroupId: eventObj.extendedProps.recurrenceGroup?.id,
  });
  const originalEvent = {
    id: eventObj.id,
    title: eventObj.title,
    price: eventObj.extendedProps.price,
    maximumCapacity: eventObj.extendedProps.maximumCapacity,
    description: eventObj.extendedProps.description,
    locationId: locationId,
    recurrenceGroupId: eventObj.extendedProps.recurrenceGroup?.id,
  };

  function areObjectsEqual(obj1, obj2) {
    const obj1Keys = Object.keys(obj1);
    const obj2Keys = Object.keys(obj2);

    if (obj1Keys.length !== obj2Keys.length) {
      return false;
    }

    for (let key of obj1Keys) {
      if (obj1[key] !== obj2[key]) {
        return false;
      }
    }

    return true;
  }

  useEffect(() => {
    fetchLocations();

    async function resetModal() {
      await sleep(500);
      setReadOnly(true);
      setShowCancelOptions(false);
      setShowFrequencyOptions(false);
      setEventsOverlap(false);
    }
    console.log(changeEventRequest);
    resetModal();
  }, [isOpen]);

  useEffect(() => {
    setFormHasErrors(changeEventRequest.title === "");
    console.log("change event request --------------");
    console.log(changeEventRequest);
    console.log("--------------------------------------");
    console.log(locationsOptions);
  }, [changeEventRequest]);

  const handleChangeEventRequest = (field, value) => {
    if (field === "locationId") {
      setEventsOverlap(false);
    }
    console.log(field);
    console.log(value);
    setChangeEventRequest((prev) => ({ ...prev, [field]: value }));
  };

  const alert = useAlert();
  const frequency = recurrenceOptions.find((option) => {
    if (eventObj.extendedProps.recurrenceGroup == null) {
      return option.value == "NEVER";
    } else {
      return option?.value === eventObj.extendedProps.recurrenceGroup.frequency;
    }
  });

  function sleep(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
  function submitChangeSeriesRequest() {
    console.log("changing series");
    axios
      .put(
        host + updateRecurrenceGroup + originalEvent.recurrenceGroupId,
        changeEventRequest
      )
      .then((response) => {
        if (response.status === 200) {
          reloadEvents();
          alert.success("Série byla aktualizována.");
          handleHide();
        }
      })
      .catch((error) => {
        let status = error.response.status;
        if (status === 409) {
          setEventsOverlap(true);
          viewOnlyDefaultDetailsModal();
          // alert.error("Sérii nebylo možné přesunout kvůli časovým kolizím.");
        } else {
          console.log(status);
          alert.error("Sérii nebylo možné aktualizovat.");
          handleHide();
        }
      });
    console.log("reload events fired");

    // handleHide();
  }

  function submitChangeSingleEventRequest() {
    axios
      .put(
        host + updateEventEndpoint + changeEventRequest.id,
        changeEventRequest
      )
      .then((response) => {
        if (response.status === 200) {
          reloadEvents();
          alert.success("Událost byla aktualizována.");
          handleHide();
        }
      })
      .catch((error) => {
        if (error.response.status === 409) {
          // alert.error("Událost nelze přesunout kvůli časovým kolizím.");
          setEventsOverlap(true);
          viewOnlyDefaultDetailsModal();
        } else {
          alert.error("Událost se nepodařilo aktualizovat.");
          handleHide();
        }
      });

    // handleHide();
  }

  function submitChangeEventRequest() {
    if (areObjectsEqual(originalEvent, changeEventRequest)) {
      console.log("equal");
      handleHide();
      alert.info("Nebyly provedeny žádné změny.");
      return;
    }
    if (eventObj.extendedProps.recurrenceGroup.frequency != "NEVER") {
      viewOnlyFrequencyOptionsModal();
    } else {
      submitChangeSingleEventRequest();
    }
  }

  function fetchLocations() {
    axios.get(host + locationsEndpoint).then((response) => {
      if (response.status === 200) {
        let locationOptions = response.data.map((location) => ({
          value: location.id,
          label: location.name,
        }));

        setLocationsOptions(locationOptions);
      }
    });
  }

  function cancelEventGroup() {
    let groupId = eventObj.extendedProps?.recurrenceGroup?.id;
    if (groupId) {
      axios
        .delete(host + cancelEventsByGroupId + groupId)
        .then((response) => {
          if (response.status === 200) {
            console.log("removed group");
            reloadEvents();
            alert.success("Série událostí byla zrušena.");
          }
        })
        .catch((err) => console.log(err));
    }
    handleHide();
  }

  function handleEdit() {
    setReadOnly(false);
  }

  function cancelSingleEvent() {
    axios
      .delete(host + cancelEventById + eventObj.id)
      .then((response) => {
        if (response.status === 200) {
          reloadEvents();
          console.log("canceling :" + eventObj);
          alert.success(
            "Událost " + eventObj?.extendedProps?.title + " byla zrušena."
          );
        }
      })
      .catch((error) => {
        alert.error("Událost nelze zrušit.");
      });
    handleHide();
  }

  function handleDelete() {
    if (eventObj?.extendedProps?.recurrenceGroup != null) {
      viewOnlyCancelOptionsModal();
    } else {
      viewOnlyCancelConfirmationModal();
    }
  }

  return (
    <Modal isOpen={isOpen} on backdrop="static" size="md" centered>
      {showDefaultDetailsModal && (
        <>
          <ModalHeader>Detail události</ModalHeader>
          <ModalBody>
            <FormGroup>
              <Row>
                <Col>
                  <Label for="title">Název</Label>
                  <Input
                    type="text"
                    name="title"
                    placeholder="Lekce jógy"
                    defaultValue={eventObj.title}
                    onChange={(e) =>
                      handleChangeEventRequest(e.target.name, e.target?.value)
                    }
                    invalid={changeEventRequest.title === ""}
                    disabled={readOnly}
                  />
                </Col>
                {!readOnly && (
                  <Col>
                    <FormGroup>
                      <Label for="locationId">Místo konání</Label>

                      <InputGroup>
                        <Form.Control
                          as="select"
                          name="locationId"
                          defaultValue={
                            changeEventRequest.locationId
                              ? changeEventRequest.locationId
                              : locationId
                          }
                          onChange={(e) =>
                            handleChangeEventRequest(
                              "locationId",
                              e.target.value
                            )
                          }
                          isInvalid={eventsOverlap}
                        >
                          {locationsOptions.map((location) => (
                            <option key={location.value} value={location.value}>
                              {location.label}
                            </option>
                          ))}
                        </Form.Control>
                        {eventsOverlap && (
                          <Form.Control.Feedback type="invalid">
                            Událost nelze přesunout kvůli časovým kolizím
                          </Form.Control.Feedback>
                        )}
                      </InputGroup>
                    </FormGroup>
                  </Col>
                )}
              </Row>

              <FormFeedback>Název nesmí být prázdný</FormFeedback>
            </FormGroup>

            <FormGroup>
              <Row>
                <Col sm="6">
                  <Label for="start">Od</Label>
                  <Input
                    type="datetime-local"
                    name="start"
                    value={eventObj.start.toISOString().slice(0, 16)}
                    readOnly
                  />
                </Col>

                <Col sm="6">
                  <Label for="end">Do</Label>
                  <Input
                    type="datetime-local"
                    name="end"
                    value={eventObj.end.toISOString().slice(0, 16)}
                    readOnly
                  />
                </Col>
              </Row>
            </FormGroup>
            <FormGroup>
              <Row>
                <Col>
                  <FormGroup>
                    <Label for="recurence">Opakovat</Label>
                    <Select
                      isSearchable={false}
                      value={frequency}
                      options={recurrenceOptions}
                      name="frequency"
                      isDisabled
                    />
                  </FormGroup>
                </Col>
                <Col>
                  <Label for="price">Cena</Label>
                  <Input
                    type="number"
                    step="20"
                    name="price"
                    placeholder="200"
                    defaultValue={eventObj.extendedProps.price}
                    min={0}
                    disabled={readOnly}
                    onChange={(e) =>
                      handleChangeEventRequest(e.target.name, e.target?.value)
                    }
                  />
                </Col>
                <Col>
                  <Label for="capacity">Kapacita</Label>
                  <Input
                    type="number"
                    name="maximumCapacity"
                    placeholder="4"
                    min={eventObj.extendedProps.maximumCapacity}
                    defaultValue={eventObj.extendedProps.maximumCapacity}
                    disabled={readOnly}
                    onChange={(e) =>
                      handleChangeEventRequest(e.target.name, e.target?.value)
                    }
                  />
                </Col>
              </Row>
            </FormGroup>

            <FormGroup>
              <Row>
                {frequency?.value === "WEEKLY" && (
                  <Col>
                    <FormGroup>
                      <Label for="endRecurrence">Dny</Label>

                      <DaysSelector
                        onChange={() => {}}
                        days={
                          eventObj?.extendedProps.recurrenceGroup.daysOfWeek
                        }
                        isDisabled={true}
                      />
                    </FormGroup>
                  </Col>
                )}
                <Col>
                  {frequency?.value !== "NEVER" && (
                    <FormGroup>
                      <Label for="endRecurrence">Opakovat do</Label>
                      <Input
                        type="date"
                        name="endDate"
                        placeholder=""
                        value={
                          eventObj?.extendedProps?.recurrenceGroup?.endDate
                        }
                        readOnly
                      />
                    </FormGroup>
                  )}
                </Col>
              </Row>
            </FormGroup>

            <FormGroup>
              <Label for="description">Popis</Label>
              <Input
                type="text"
                name="description"
                placeholder="Lekce jógy pod vedením Jaroslava Bašty."
                defaultValue={eventObj.extendedProps.description}
                disabled={readOnly}
                onChange={(e) =>
                  handleChangeEventRequest(e.target.name, e.target.value)
                }
              />
            </FormGroup>
          </ModalBody>
          <ModalFooter>
            {
              <Button color="secondary" onClick={handleHide}>
                Zavřít
              </Button>
            }
            {readOnly && (
              <Button color="danger" onClick={handleDelete}>
                Smazat
              </Button>
            )}
            {readOnly && (
              <Button color="primary" onClick={handleEdit}>
                Upravit
              </Button>
            )}

            {readOnly || (
              <Button
                color="primary"
                onClick={submitChangeEventRequest}
                disabled={formHasErrors}
              >
                Uložit
              </Button>
            )}
          </ModalFooter>
        </>
      )}
      {showFrequencyOptions && (
        <>
          <ModalHeader>Aktualizovat události</ModalHeader>
          <ModalBody>
            {"Přejete si aktualizovat celou sérii událostí či pouze tuto událost " +
              eventObj.title +
              "?"}
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={handleHide}>
              Zavřít
            </Button>
            <Button color="primary" onClick={submitChangeSingleEventRequest}>
              Aktualizovat událost
            </Button>
            <Button color="danger" onClick={submitChangeSeriesRequest}>
              Aktualizovat sérii
            </Button>
          </ModalFooter>
        </>
      )}

      {showCancelOptions && (
        <>
          <ModalHeader>Zrušit události</ModalHeader>
          <ModalBody>
            {"Přejete si zrušit pouze tento výskyt události či všechny budoucí události série " +
              eventObj.title +
              " a související rezervace?"}
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={handleHide}>
              Zavřít
            </Button>
            <Button color="primary" onClick={cancelSingleEvent}>
              Tato událost
            </Button>
            <Button color="danger" onClick={cancelEventGroup}>
              Budoucí události
            </Button>
          </ModalFooter>
        </>
      )}

      {showCancelConfirmationModal && (
        <>
          <ModalHeader>Zrušit události</ModalHeader>
          <ModalBody>
            {"Opravdu si přejete zrušit událost " + eventObj.title + "?"}
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={handleHide}>
              Zavřít
            </Button>
            <Button color="primary" onClick={cancelSingleEvent}>
              Zrušit událost
            </Button>
          </ModalFooter>
        </>
      )}
    </Modal>
  );
};

export default DetailsModal;
