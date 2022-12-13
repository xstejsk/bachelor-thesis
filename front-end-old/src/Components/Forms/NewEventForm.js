import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-daterangepicker/daterangepicker.css";

import React, { useState } from "react";
import {
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Button,
  FormGroup,
  Label,
  Input,
  Col,
  Row,
} from "reactstrap";
import Select from "react-select";
import DateRangePicker from "react-bootstrap-daterangepicker";
import DaysSelector from "../DaysSelector";
import axios from "axios";
import { host, newEventEndpoint } from "../../util/EndpointConfig";
// import createReactClass from "create-react-class";

const NewEventForm = ({ isOpen, setIsOpen, calendarRef, locationId }) => {
  const [eventTitle, setEvenTitle] = useState("");
  const [startDateTime, setStarDateTime] = useState(new Date());
  const [endDateTime, setEndDateTime] = useState(new Date());
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState(100);
  const [capacity, setCapacity] = useState(1);

  const [endRecurrenceDate, setEndReccurenceDate] = useState(new Date());
  const [days, setDays] = useState([]);
  const recurrenceOptions = [
    { value: "NEVER", label: "Nikdy" },
    { value: "WEEKLY", label: "Týdně" },
    { value: "MONTHLY", label: "Měsíčně" },
  ];
  const [recurrence, setRecurrence] = useState(recurrenceOptions[0].value);
  function handleCancel() {
    console.log(days);
    setIsOpen(false);
  }

  function handleSubmit() {
    const newEvent = {
      title: eventTitle,
      start: startDateTime.toISOString(),
      end: endDateTime.toISOString(),
      allDay: false,
      capacity: capacity,
      price: price,
      description: description,
      isFull: false,
      locationId: locationId,
      recurrenceGroup: {
        frequency: recurrence,
        daysOfWeek: days,
        endDate: endRecurrenceDate,
      },
    };
    axios
      .post(host + newEventEndpoint, newEvent)
      .then((response) => {
        if (response.status === 201) {
          let calendarApi = calendarRef.current.getApi();
          let data = response.data;
          console.log(data);
          for (let i = 0; i <= data.length; i++) {
            console.log(data[i]);
            calendarApi.addEvent(data[i]);
          }
        } else {
          console.log("fail");
          console.log(newEvent);
        }
      })
      .catch((err) => console.log(err));
    handleCancel();
  }

  return (
    <Modal isOpen={isOpen} on backdrop="static" size="md">
      <ModalHeader>Nová událost</ModalHeader>
      <ModalBody>
        <FormGroup>
          <Label for="title">Název</Label>
          <Input
            type="text"
            name="title"
            placeholder="Název"
            value={eventTitle}
            onChange={(e) => setEvenTitle(e.target.value)}
          />
        </FormGroup>

        <FormGroup>
          <Row>
            <Col>
              <Label for="price">Cena</Label>
              <Input
                type="number"
                step="20"
                name="price"
                placeholder="Cena"
                value={price}
                min={0}
                onChange={(e) => setPrice(e.target.value)}
              />
            </Col>
            <Col>
              <Label for="capacity">Kapacita</Label>
              <Input
                type="number"
                name="capacity"
                placeholder="Kapacita"
                min={1}
                value={capacity}
                onChange={(e) => setCapacity(e.target.value)}
              />
            </Col>
          </Row>
        </FormGroup>
        <FormGroup>
          <Row>
            <Col>
              <Label for="range">Od - do</Label>
              <DateRangePicker
                initialSettings={{
                  locale: {
                    // format: "H:m",
                    format: "MM/DD HH:mm",
                    separator: " - ",
                    applyLabel: "Použít",
                    cancelLabel: "Zrušit",
                    fromLabel: "Od",
                    toLabel: "Do",
                    customRangeLabel: "Vlastní",
                    daysOfWeek: ["Ne", "Po", "Út", "St", "Čt", "Pá", "So"],
                    timePicker24Hour: true,
                    monthNames: [
                      "Leden",
                      "Únor",
                      "Březen",
                      "Duben",
                      "Květen",
                      "Červen",
                      "Červenec",
                      "Srpen",
                      "Září",
                      "Říjen",
                      "Listopad",
                      "Prosinec",
                    ],
                    firstDay: 1,
                  },
                  startDate: startDateTime,
                  endDate: endDateTime,
                  timePicker: true,
                }}
                onApply={(event, picker) => {
                  setStarDateTime(new Date(picker.startDate));
                  setEndDateTime(new Date(picker.endDate));
                }}
              >
                <input className="form-control" type="text" />
              </DateRangePicker>
            </Col>
            <Col>
              <FormGroup>
                <Label for="recurence">Opakovat</Label>
                <Select
                  isSearchable={false}
                  defaultValue={recurrenceOptions[0]}
                  options={recurrenceOptions}
                  onChange={(element) => setRecurrence(element.value)}
                />
              </FormGroup>
            </Col>
          </Row>
        </FormGroup>

        <FormGroup>
          <Row>
            {recurrence === "WEEKLY" && (
              <Col>
                <FormGroup>
                  <Label for="endRecurrence">Dny</Label>
                  <DaysSelector
                    onChange={(value) => {
                      setDays(value);
                      console.log(days);
                    }}
                  />
                </FormGroup>
              </Col>
            )}
            <Col>
              {recurrence !== "NEVER" && (
                <FormGroup>
                  <Label for="endRecurrence">Do</Label>
                  <Input
                    required={true}
                    type="date"
                    name="endRecurrenceDate"
                    placeholder="endRecurrenceDate"
                    value={endRecurrenceDate}
                    onChange={(e) => setEndReccurenceDate(e.target.value)}
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
            placeholder="Popis"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </FormGroup>
      </ModalBody>

      <ModalFooter>
        {
          <Button color="secondary" onClick={handleCancel}>
            Zrušit
          </Button>
        }
        {
          <Button color="primary" onClick={handleSubmit}>
            Uložit
          </Button>
        }
      </ModalFooter>
    </Modal>
  );
};

export default NewEventForm;
