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
  Col,
  Row,
} from "reactstrap";
import Select from "react-select";
import DateRangePicker from "react-bootstrap-daterangepicker";
import DaysSelector from "../DaysSelector";
import axios from "axios";
import { host, newEventEndpoint } from "../../util/EndpointConfig";
import { useAlert } from "react-alert";
import { event } from "jquery";

const NewEventForm = ({ handleHide, isOpen, locationId, addEvents }) => {
  const [eventTitle, setEvenTitle] = useState("");
  const [startDateTime, setStarDateTime] = useState(new Date());
  const [endDateTime, setEndDateTime] = useState(new Date());
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState(100);
  const [capacity, setCapacity] = useState(1);
  const alert = useAlert();

  const [endRecurrenceDate, setEndReccurenceDate] = useState(new Date());
  const [days, setDays] = useState([]);
  const recurrenceOptions = [
    { value: "NEVER", label: "Nikdy" },
    { value: "WEEKLY", label: "Týdně" },
    { value: "MONTHLY", label: "Měsíčně" },
  ];
  const [recurrence, setRecurrence] = useState(recurrenceOptions[0].value);
  function handleCancel() {
    //calendarRef.current.rerenderEvents();
    handleHide();
  }

  useEffect(() => {
    console.log("duration ----------");
    console.log(startDateTime);
    console.log(endDateTime);
  }, [startDateTime, endDateTime]);

  function handleSubmit() {
    const newEvent = {
      title: eventTitle,
      start: startDateTime,
      end: endDateTime,
      startString: startDateTime.toISOString(),
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
        console.log("posting new event------");
        console.log(newEvent);
        console.log(new Date().getTimezoneOffset());
        console.log("----------");
        let data = [];
        if (response.status === 201) {
          // let calendarApi = calendarRef.current.getApi();
          data = response.data;
          if (data.length !== 0) {
            addEvents(data);
          }
        }
      })
      .catch((error) => {
        if (error.response.status === 409) {
          let arrayOfIds = error.response.data;
          alert.error(
            "Událost se nepodařilo vytvořit, protože se kryje s událostmi s ID: " +
              arrayOfIds.join(", ")
          );
        } else if (error.status.code === 400) {
          alert.error("Události se nepodařilo vytvořit.");
        }
      });
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
              <Label for="startTime">Od - do</Label>
              {/* <Input
                type="date"
                name="startTime"
                placeholder="1.1.2021"
                min={new Date().toISOString().substr(0, 10)}
              />
            </Col> */}
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
                  console.log("picker date------------");
                  console.log(picker.startDate);
                  console.log("picker date time------------");
                  console.log(picker.startDateTime);
                  console.log("picker date from date time------------");
                  console.log(new Date(picker.startDate));
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
