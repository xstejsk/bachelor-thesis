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
import { Form } from "react-bootstrap";

const DetailsModal = ({
  handleHide,
  isOpen,
  locationId,
  eventObj,
  updateEvents,
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
  const [changeEventRequest, setChangeEventRequest] = useState({
    id: eventObj.id,
    title: eventObj.title,
    price: eventObj.extendedProps.price,
    capacity: eventObj.extendedProps.capacity,
    description: eventObj.extendedProps.description,
    locationId: locationId,
    recurrenceGroupId: eventObj.extendedProps.recurrenceGroup.id,
  });
  const originalEvent = {
    id: eventObj.id,
    title: eventObj.title,
    price: eventObj.extendedProps.price,
    capacity: eventObj.extendedProps.capacity,
    description: eventObj.extendedProps.description,
    locationId: locationId,
    recurrenceGroupId: eventObj.extendedProps.recurrenceGroup.id,
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

  const [showCancelOptions, setShowCancelOptions] = useState(false);

  useEffect(() => {
    fetchLocations();

    async function resetModal() {
      await sleep(500);
      setReadOnly(true);
      setShowCancelOptions(false);
      setShowFrequencyOptions(false);
    }
    console.log(changeEventRequest);
    resetModal();
  }, [isOpen]);

  useEffect(() => {
    setFormHasErrors(changeEventRequest.title === "");
    console.log(changeEventRequest);
  }, [changeEventRequest]);

  const handleChangeEventRequest = (field, value) => {
    setChangeEventRequest((prev) => ({ ...prev, [field]: value }));
  };

  const alert = useAlert();
  const frequency = recurrenceOptions.find(
    (option) =>
      option.value === eventObj.extendedProps.recurrenceGroup.frequency
  );

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
          alert.success("Série byla aktualizována.");
        }
      })
      .catch((error) => {
        let status = error.response.status;
        if (status === 409) {
          alert.error("Sérii nebylo možné přesunout kvůli časovým kolizím.");
        } else {
          console.log(status);
          alert.error("Sérii nebylo možné aktualizovat.");
        }
      });
    handleHide();
  }

  function submitChangeSingleEventRequest() {
    axios
      .put(
        host + updateEventEndpoint + changeEventRequest.id,
        changeEventRequest
      )
      .then((response) => {
        if (response.status === 200) {
          alert.success("Událost byla aktualizována.");
          reloadEvents();
        }
      })
      .catch((error) => {
        if (error.response.status === 409) {
          alert.error("Událost nelze přesunout kvůli časovým kolizím.");
        } else {
          alert.error("Událost se nepodařilo aktualizovat.");
        }
      });
    handleHide();
  }

  function submitChangeEventRequest() {
    if (areObjectsEqual(originalEvent, changeEventRequest)) {
      console.log("equal");
      handleHide();
      alert.info("Nebyly provedeny žádné změny.");
      return;
    } else {
      console.log(originalEvent);
      console.log("non equal");
      console.log(changeEventRequest);
    }
    if (eventObj.extendedProps.recurrenceGroup.frequency != "NEVER") {
      setShowFrequencyOptions(true);
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
        .put(host + cancelEventsByGroupId + groupId)
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
      .put(host + cancelEventById + eventObj.id)
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
    if (eventObj?.extendedProps?.recurrenceGroup.frequency !== "NEVER") {
      setShowCancelOptions(true);
    } else {
      cancelSingleEvent();
    }
  }

  return (
    <Modal isOpen={isOpen} on backdrop="static" size="md" centered>
      {!showCancelOptions && !showFrequencyOptions && (
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
                      handleChangeEventRequest(e.target.name, e.target.value)
                    }
                    invalid={changeEventRequest.title === ""}
                    disabled={readOnly}
                  />
                </Col>
                {!readOnly && (
                  <Col>
                    <FormGroup>
                      <Label for="locationId">Místo konání</Label>
                      <Select
                        name="locationId"
                        defaultValue={locationsOptions.find(
                          (location) => location.value == locationId
                        )}
                        options={locationsOptions}
                        onChange={(element) =>
                          handleChangeEventRequest("locationId", element.value)
                        }
                      />
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
                      handleChangeEventRequest(e.target.name, e.target.value)
                    }
                  />
                </Col>
                <Col>
                  <Label for="capacity">Kapacita</Label>
                  <Input
                    type="number"
                    name="capacity"
                    placeholder="4"
                    min={1}
                    defaultValue={eventObj.extendedProps.capacity}
                    disabled={readOnly}
                    onChange={(e) =>
                      handleChangeEventRequest(e.target.name, e.target.value)
                    }
                  />
                </Col>
              </Row>
            </FormGroup>

            <FormGroup>
              <Row>
                {frequency.value === "WEEKLY" && (
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
                  {frequency.value !== "NEVER" && (
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
      {showFrequencyOptions && !showCancelOptions && (
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

      {showCancelOptions && !showFrequencyOptions && (
        <>
          <ModalHeader>Zrušit události</ModalHeader>
          <ModalBody>
            {"Přejete si zrušit celou sérii událostí či pouze tuto událost " +
              eventObj.title +
              " a související rezervace?"}
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={handleHide}>
              Zavřít
            </Button>
            <Button color="primary" onClick={cancelSingleEvent}>
              Zrušit událost
            </Button>
            <Button color="danger" onClick={cancelEventGroup}>
              Zrušit sérii
            </Button>
          </ModalFooter>
        </>
      )}
    </Modal>
  );
};

export default DetailsModal;
