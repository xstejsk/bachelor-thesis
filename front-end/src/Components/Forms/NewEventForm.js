import "bootstrap/dist/css/bootstrap.css";
import React, { useEffect, useState } from "react";
import {
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Button,
  FormGroup,
  FormFeedback,
  Label,
  Input,
} from "reactstrap";
import { Row, Col } from "react-bootstrap";
import Select from "react-select";
import DaysSelector from "../DaysSelector";
import axios from "axios";
import { host, newEventEndpoint } from "../../util/EndpointConfig";
import { useAlert } from "react-alert";

const NewEventForm = ({ handleHide, isOpen, locationId, reloadEvents }) => {
  const recurrenceOptions = [
    { value: "NEVER", label: "Nikdy" },
    { value: "WEEKLY", label: "Týdně" },
    { value: "MONTHLY", label: "Měsíčně" },
  ];
  const alert = useAlert();
  const [recurrenceGroup, setRecurrenceGroup] = useState({
    frequency: recurrenceOptions[0].value,
    daysOfWeek: ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
    endDate: undefined,
  });
  const [event, setEvent] = useState({
    title: "",
    start: undefined,
    end: undefined,
    description: "",
    locationId: locationId,
    price: 100,
    capacity: 1,
    recurrenceGroup: recurrenceGroup,
  });

  const handleChangeEvent = (field, value) => {
    setEvent((prev) => ({ ...prev, [field]: value }));
  };

  const handleChangeRecurrenceGroup = (field, value) => {
    setRecurrenceGroup((prev) => ({ ...prev, [field]: value }));
  };

  useEffect(() => {
    setFormHasErrors(
      event.title === "" ||
        event.end == undefined ||
        event.start == undefined ||
        new Date(event.start) > new Date(event.end) ||
        (recurrenceGroup.frequency != "NEVER" &&
          recurrenceGroup.frequency == undefined)
    );
  }, [event]);

  useEffect(() => {
    setEvent((prev) => ({ ...prev, ["recurrenceGroup"]: recurrenceGroup }));
  }, [recurrenceGroup]);

  const [recurrence, setRecurrence] = useState(recurrenceOptions[0].value);
  const [formHasErrors, setFormHasErrors] = useState(true);

  function formatDate(date) {
    return (
      date.getFullYear() +
      "-" +
      (date.getMonth() + 1).toString().padStart(2, "0") +
      "-" +
      date.getDate().toString().padStart(2, "0") +
      "T" +
      date.getHours().toString().padStart(2, "0") +
      ":" +
      date.getMinutes().toString().padStart(2, "0")
    );
  }
  useEffect(() => {
    setRecurrence("NEVER");
  }, [isOpen]);

  function handleSubmit() {
    axios
      .post(host + newEventEndpoint, event)
      .then((response) => {
        let data = [];
        if (response.status === 201) {
          data = response.data;
          if (data.length == 1) {
            alert.info("Událost byla vytvořena.");
          } else if (data.length > 1) {
            alert.info("Série událostí byla vytvořena.");
          }
          reloadEvents();
        }
      })
      .catch((error) => {
        console.log(error.response.status);
        if (error.response.status === 409) {
          let arrayOfIds = error.response.data.map((event) => event.id);
          alert.error(
            "Událost se nepodařilo vytvořit, protože se kryje s událostmi s ID: " +
              arrayOfIds.join(", ")
          );
        } else if (error.response.status === 400) {
          alert.error("Události se nepodařilo vytvořit.");
        }
      });
    handleHide();
  }

  return (
    <Modal isOpen={isOpen} on backdrop="static" size="md" centered>
      <ModalHeader>Nová událost</ModalHeader>
      <ModalBody>
        <FormGroup>
          <Label for="title">Název</Label>
          <Input
            type="text"
            name="title"
            placeholder="Lekce jógy"
            value={event.title}
            onChange={(e) => handleChangeEvent(e.target.name, e.target.value)}
            invalid={event.title === ""}
          />
          <FormFeedback>Název nesmí být prázdný</FormFeedback>
        </FormGroup>

        <FormGroup>
          <Row>
            <Col sm="6">
              <Label for="start">Od</Label>
              <Input
                type="datetime-local"
                name="start"
                placeholder={new Date().toISOString().substr(0, 16)}
                min={new Date().toISOString().substr(0, 16)}
                onChange={(e) =>
                  handleChangeEvent(e.target.name, e.target.value)
                }
                invalid={event.start == undefined}
              />
              <FormFeedback>Vyberte začátek </FormFeedback>
            </Col>

            <Col sm="6">
              <Label for="end">Do</Label>
              <Input
                type="datetime-local"
                name="end"
                placeholder={new Date().toISOString().substr(0, 16)}
                min={event.start}
                onChange={(e) =>
                  handleChangeEvent(e.target.name, e.target.value)
                }
                invalid={
                  event.end == undefined ||
                  new Date(event.end) < new Date(event.start)
                }
              />
              {event.end == undefined && (
                <FormFeedback>Vyberte konec</FormFeedback>
              )}
              {new Date(event.end) < new Date(event.start) && (
                <FormFeedback>Konec nemůže být před začátkem</FormFeedback>
              )}
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
                  defaultValue={recurrenceOptions[0]}
                  options={recurrenceOptions}
                  name="frequency"
                  onChange={(e) => {
                    handleChangeRecurrenceGroup("frequency", e.value);
                  }}
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
                value={event.price}
                min={0}
                onChange={(e) =>
                  handleChangeEvent(e.target.name, e.target.value)
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
                value={event.capacity}
                onChange={(e) =>
                  handleChangeEvent(e.target.name, e.target.value)
                }
              />
            </Col>
          </Row>
        </FormGroup>

        <FormGroup>
          <Row>
            {recurrenceGroup.frequency === "WEEKLY" && (
              <Col>
                <FormGroup>
                  <Label for="endRecurrence">Dny</Label>

                  <DaysSelector
                    onChange={(value) => {
                      setRecurrenceGroup((prev) => ({
                        ...prev,
                        ["daysOfWeek"]: value,
                      }));
                    }}
                    days={recurrenceGroup.daysOfWeek}
                  />
                </FormGroup>
              </Col>
            )}
            <Col>
              {recurrenceGroup.frequency !== "NEVER" && (
                <FormGroup>
                  <Label for="endRecurrence">Opakovat do</Label>
                  <Input
                    required={true}
                    type="date"
                    name="endDate"
                    placeholder=""
                    value={recurrenceGroup.endDate}
                    min={new Date().toISOString().substr(0, 10)}
                    onChange={(e) =>
                      handleChangeRecurrenceGroup(e.target.name, e.target.value)
                    }
                    invalid={recurrenceGroup.endDate == undefined}
                  />
                  <FormFeedback>Vyberte konec</FormFeedback>
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
            value={event.description}
            onChange={(e) => handleChangeEvent(e.target.name, e.target.value)}
          />
        </FormGroup>
      </ModalBody>

      <ModalFooter>
        {
          <Button color="secondary" onClick={handleHide}>
            Zavřít
          </Button>
        }
        {
          <Button
            color="primary"
            onClick={handleSubmit}
            disabled={formHasErrors}
          >
            Uložit
          </Button>
        }
      </ModalFooter>
    </Modal>
  );
};

export default NewEventForm;
