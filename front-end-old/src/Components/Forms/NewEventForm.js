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

const NewEventForm = ({ isOpen, setIsOpen, onSubmit, calendarRef }) => {
  const [eventTitle, setEvenTitle] = useState("");
  const [startDateTime, setStarDateTime] = useState(new Date());
  const [endDateTime, setEndDateTime] = useState(new Date());
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState(100);
  const [capacity, setCapacity] = useState(1);
  const [location, setLocation] = useState(""); // will be taken from the callendar context - each location has its own callendar
  const recurrenceOptions = [
    { value: "never", label: "Nikdy" },
    { value: "daily", label: "Denně" },
    { value: "weekly", label: "Týdně" },
    { value: "monthly", label: "Měsíčně" },
  ];
  const [recurrence, setRecurrence] = useState(recurrenceOptions[0].value);
  const [endRecurrenceDate, setEndReccurenceDate] = useState(new Date());
  const [days, setDays] = useState([]); // array mapping days to numbers, monday - 0

  function handleCancel() {
    console.log(days);
    setIsOpen(false);
  }

  function handleSubmit() {
    // console.log(state.selectInfo.view.calendar);
    const newEvent = {
      title: eventTitle,
      startTime: startDateTime.toISOString(),
      endTime: endDateTime.toISOString(),
      allDay: false,
      capacity: capacity,
      price: price,
      description: description,
      isFull: false,
      locationName: "Sal 1",
    };
    axios
      .post(host + newEventEndpoint, newEvent)
      .then((response) => {
        if (response.status === 201) {
          console.log(response.data);
        } else {
          console.log("fail");
        }
      })
      .catch((err) => console.log(err));
    // console.log(newEvent);

    // let calendarApi = calendarRef.current.getApi();
    // let calendarApi = selectInfo.view.calendar

    // calendarApi.addEvent(newEvent);
    handleCancel();
  }

  return (
    <Modal isOpen={isOpen} backdrop="static" size="md">
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
                  console.log(
                    "picker",
                    picker.startDate.toISOString(),
                    picker.endDate.toISOString()
                  );
                  // setStarDateTime(new Date(picker.startDate));
                  // setEndDateTime(new Date(picker.endDate));
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

        {recurrence !== recurrenceOptions[0].value && (
          <FormGroup>
            <Row>
              <Col>
                <FormGroup>
                  <Label for="endRecurrence">Dny</Label>
                  <DaysSelector onChange={(value) => setDays(value)} />
                </FormGroup>
              </Col>
              <Col>
                <FormGroup>
                  <Label for="endRecurrence">Do</Label>
                  <Input
                    type="date"
                    name="endRecurrenceDate"
                    placeholder="endRecurrenceDate"
                    value={endRecurrenceDate}
                    onChange={(e) => setEndReccurenceDate(e.target.value)}
                  />
                </FormGroup>
              </Col>
            </Row>
          </FormGroup>
        )}
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
