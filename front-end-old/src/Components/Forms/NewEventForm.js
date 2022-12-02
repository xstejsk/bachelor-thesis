import React, { useState } from "react";
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from "reactstrap";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import DateRangePicker from "react-bootstrap-daterangepicker";
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-daterangepicker/daterangepicker.css";

const NewEventForm = ({
  title = "Title",
  isOpen,
  setIsOpen,
  toggle,
  onCancel,
  cancelText,
  onSubmit,
  submitText,
  onDelete,
  deleteText,
  children,
}) => {
  const [subject, setSubject] = useState("");
  const [startTime, setStartTime] = useState(new Date());
  const [endTime, setEndTime] = useState(new Date());
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState(100);
  const [capacity, setCapacity] = useState(1);
  const [location, setLocation] = useState("");

  function handleCancel() {
    setIsOpen(false);
  }

  function handleSubmit() {
    // console.log(state.selectInfo.view.calendar);
    // const newEvent = {
    //   id: nanoid(),
    //   title,
    //   start: state.selectInfo?.startStr || start.toISOString(),
    //   end: state.selectInfo?.endStr || end.toISOString(),
    //   allDay: state.selectInfo?.allDay || false,
    // };
    // // console.log(newEvent);

    // let calendarApi = calendarRef.current.getApi();
    // // let calendarApi = selectInfo.view.calendar

    // calendarApi.addEvent(newEvent);
    handleCancel();
  }
  const onChangeSubject = (e) => {
    setSubject(e.target.value);
  };

  return (
    <Modal isOpen={isOpen} backdrop="static">
      <ModalHeader>Nová událost</ModalHeader>
      <ModalBody>
        <div className="form-floating">
          <input
            type="text"
            className="form-control"
            id="subject"
            placeholder="Subjekt"
            onChange={(e) => setSubject(e.target.value)}
          />
          <label htmlFor="subject">Subjekt</label>
        </div>
        <label for="exampleEmail">Od - do</label>
        <DateRangePicker
          timePicker
          timePicker24Hour={true}
          pick12HourFormat={false}
          initialSettings={{
            locale: {
              // format: "H:m",
              format: "MM/DD/YYYY HH:mm",
              separator: " - ",
              applyLabel: "Použít",
              cancelLabel: "Zrušit",
              fromLabel: "Od",
              toLabel: "Do",
              customRangeLabel: "Vlastní",
              daysOfWeek: ["Ne", "Po", "Út", "St", "Čt", "Pá", "So"],
              timePicker24Hour: true,
              pick12HourFormat: false,
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
            startDate: startTime,
            endDate: endTime,
            timePicker: true,
          }}
          onApply={(event, picker) => {
            // console.log(
            //   "picker",
            //   picker.startDate.toISOString(),
            //   picker.endDate.toISOString()
            // );
            // setStartTime(new Date(picker.startDate));
            // setEndTime(new Date(picker.endDate));
          }}
        >
          <input className="form-control" type="text" />
        </DateRangePicker>
      </ModalBody>
      <ModalFooter>
        {
          <Button color="secondary" onClick={handleCancel}>
            Zrušit
          </Button>
        }
        {
          <Button color="primary" onClick={onSubmit}>
            Uložit
          </Button>
        }
      </ModalFooter>
    </Modal>
  );
};

export default NewEventForm;
