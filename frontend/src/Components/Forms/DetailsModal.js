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
  eventsEndpoint,
  recurrentEventsEndpoint
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
    title: eventObj.title,
    price: eventObj.extendedProps.price,
    maximumCapacity: eventObj.extendedProps.maximumCapacity,
    description: eventObj.extendedProps.description,
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
      async function resetModal() {
      await sleep(500);
      setReadOnly(true);
      setShowCancelOptions(false);
      setShowFrequencyOptions(false);
    }
    console.log(eventObj.end);
    console.log("event start-------")
    console.log(new Date(eventObj.start))
    console.log("now-------")
    console.log(new Date())
    console.log(new Date(eventObj.start) < new Date())
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
        host + recurrentEventsEndpoint + "/" + originalEvent.recurrenceGroupId,
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
          viewOnlyDefaultDetailsModal();
          alert.error("Nová kapacita události nesmí být menší než ta původní.");
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
        host + eventsEndpoint + "/" + originalEvent.id,
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
        .delete(host + recurrentEventsEndpoint + "/" + groupId)
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
      .delete(host + eventsEndpoint + "/" + eventObj.id)
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
            <Form>
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
                    value={eventObj.startStr.slice(0, 16)}
                    readOnly
                  />
                </Col>

                <Col sm="6">
                  <Label for="end">Do</Label>
                  <Input
                    type="datetime-local"
                    name="end"
                    value={eventObj.endStr.slice(0, 16)}
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
            </Form>
            
          </ModalBody>
          <ModalFooter>
            {
              <Button color="secondary" onClick={handleHide}>
                Zavřít
              </Button>
            }
            {readOnly && (
              <Button color="danger" onClick={handleDelete} disabled={new Date(eventObj.start) < new Date()}>
                Smazat
              </Button>
            )}
            {readOnly && (
              <Button color="primary" onClick={handleEdit} disabled={new Date(eventObj.start) < new Date()}>
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
