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
import DaysSelector from "./DaysSelector";
import axios from "axios";
import {
  host,
  newEventEndpoint,
  cancelEventById,
  cancelEventsByGroupId,
} from "../util/EndpointConfig";
import { useAlert } from "react-alert";

const DetailsModal = ({
  handleHide,
  isOpen,
  locationId,
  addEvents,
  removeEvent,
  removeEventGroup,
  event,
}) => {
  const [readOnly, setReadOnly] = useState(true);
  const [eventTitle, setEvenTitle] = useState(event.title);
  const [startDateTime, setStarDateTime] = useState(event.start);
  const [endDateTime, setEndDateTime] = useState(event.end);
  const [description, setDescription] = useState(
    event.extendedProps?.description
  );
  const [price, setPrice] = useState(event.extendedProps?.price);
  const [capacity, setCapacity] = useState(event.extendedProps?.capacity);
  const [endRecurrenceDate, setEndReccurenceDate] = useState(
    event.recurrenceGroup?.endDate
  );
  const [days, setDays] = useState(
    event.extendedProps?.recurrenceGroup?.daysOfWeek
  );
  const alert = useAlert();

  const recurrenceOptions = [
    { value: "NEVER", label: "Nikdy" },
    { value: "WEEKLY", label: "Týdně" },
    { value: "MONTHLY", label: "Měsíčně" },
  ];
  const [recurrence, setRecurrence] = useState(
    event.extendedProps?.recurrenceGroup?.frequency
  );
  const [showDeleteGroupModal, setShowDeleteGroupModal] = useState(false);

  const defaultRecurrence = recurrenceOptions
    .filter(
      (option) =>
        option.value === event.extendedProps?.recurrenceGroup?.frequency
    )
    .at(0);

  async function handleCancel() {
    handleHide();
    await new Promise((r) => setTimeout(r, 500));
    console.log("handle hide");
    setReadOnly(true);
  }

  function handleDeleteGroup() {
    let groupId = event.extendedProps?.recurrenceGroup?.id;
    if (groupId) {
      axios
        .put(host + cancelEventsByGroupId + groupId)
        .then((response) => {
          if (response.status === 200) {
            console.log("removed group");
            removeEventGroup(event.extendedProps?.recurrenceGroup?.id);
          }
        })
        .catch((err) => console.log(err));
    }

    setShowDeleteGroupModal(false);
    handleHide();
  }

  function handleEdit() {
    setReadOnly(false);
  }

  function handleDelete() {
    if (event?.extendedProps?.recurrenceGroup.frequency !== "NEVER") {
      setShowDeleteGroupModal(true);
    } else {
      axios
        .put(host + cancelEventById + event.id)
        .then((response) => {
          if (response.status === 200) {
            removeEvent(event.id);
            console.log("canceling :" + event);
            alert.info(
              "Událost " + event?.extendedProps?.title + " byla zrušena."
            );
          }
        })
        .catch((err) => console.log(err));
    }
    handleHide();
  }

  function handleCancelDelte() {
    setShowDeleteGroupModal(false);
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
        let data = [];
        if (response.status === 201) {
          // let calendarApi = calendarRef.current.getApi();
          data = response.data;
          if (data.length !== 0) {
            addEvents(data);
          }
        } else {
          console.log("fail");
        }
      })
      .catch((err) => console.log(err));
    handleCancel();
  }

  if (showDeleteGroupModal) {
    return (
      <Modal
        isOpen={showDeleteGroupModal}
        on
        backdrop="static"
        size="sm"
        centered={true}
      >
        <ModalHeader>Zrušit události</ModalHeader>
        <ModalBody>
          {"Opravdu si přejete zrušit celou sérii událostí " +
            event.title +
            "?"}
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={handleCancelDelte}>
            Ne
          </Button>
          <Button color="danger" onClick={handleDeleteGroup}>
            Ano
          </Button>
        </ModalFooter>
      </Modal>
    );
  } else {
    return (
      <Modal isOpen={isOpen} on backdrop="static" size="md">
        <ModalHeader>Upravit událost</ModalHeader>
        <ModalBody>
          <FormGroup>
            <Label for="title">Název</Label>
            <Input
              readOnly={readOnly}
              type="text"
              name="title"
              placeholder={eventTitle}
              value={eventTitle}
              onChange={(e) => setEvenTitle(e.target.value)}
            />
          </FormGroup>

          <FormGroup>
            <Row>
              <Col>
                <Label for="price">Cena</Label>
                <Input
                  readOnly={readOnly}
                  type="number"
                  step="20"
                  name="price"
                  placeholder={price}
                  value={price}
                  min={0}
                  onChange={(e) => setPrice(e.target.value)}
                />
              </Col>
              <Col>
                <Label for="capacity">Kapacita</Label>
                <Input
                  readOnly={readOnly}
                  type="number"
                  name="capacity"
                  placeholder={capacity}
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
                  <input
                    readOnly={readOnly}
                    className="form-control"
                    type="text"
                  />
                </DateRangePicker>
              </Col>
              <Col>
                <FormGroup>
                  <Label for="recurence">Opakovat</Label>
                  <Select
                    defaultValue={defaultRecurrence}
                    isDisabled={readOnly}
                    isSearchable={false}
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
                      defaultValue={days}
                    />
                  </FormGroup>
                </Col>
              )}
              <Col>
                {recurrence !== "NEVER" && (
                  <FormGroup>
                    <Label for="endRecurrence">Do</Label>
                    <Input
                      readOnly={readOnly}
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
              readOnly={readOnly}
              type="text"
              name="description"
              placeholder="Popis"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </FormGroup>
        </ModalBody>
        <ModalFooter>
          {readOnly && (
            <Button color="primary" onClick={handleEdit}>
              Upravit
            </Button>
          )}
          {readOnly && (
            <Button color="danger" onClick={handleDelete}>
              Smazat
            </Button>
          )}
          {
            <Button color="secondary" onClick={handleCancel}>
              Zavřít
            </Button>
          }
          {readOnly || (
            <Button color="primary" onClick={handleSubmit}>
              Uložit
            </Button>
          )}
        </ModalFooter>
      </Modal>
    );
  }
};

export default DetailsModal;
