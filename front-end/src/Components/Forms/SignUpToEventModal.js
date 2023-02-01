import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-daterangepicker/daterangepicker.css";

import React, { useContext, useState } from "react";
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from "reactstrap";
import { Context } from "../../util/GlobalState";
import axios from "axios";
// import createReactClass from "create-react-class";
import { host, newReservationEndpoint } from "../../util/EndpointConfig";

const SignUpModal = ({ clickInfo, isOpen, handleHide }) => {
  const [globalState, setGlobalState] = useContext(Context);

  function handleCancel() {
    handleHide();
  }

  function handleSignUp() {
    const event = clickInfo.event;
    console.log(clickInfo);
    const extendedProps = event?.extendedProps;
    const newReservation = {
      owner: globalState?.user,
      eventId: event.id,
      start: event["start"],
      end: event["end"],
      price: extendedProps["price"],
      title: event["title"],
    };
    console.log(newReservation);
    axios
      .post(host + newReservationEndpoint, newReservation)
      .then((response) => {
        console.log(response.status);
      })
      .catch((err) => {
        let status = err.response.status;
        let message;
        if (status === 403) {
          alert("Na událost se nyní nelze přihlásit kvůli plné kapacitě.");
        } else if (status === 409) {
          alert("Na událost již máte vytvořenou rezervaci.");
        } else if (status === 400) {
          alert("Na událost se nelze přihlásit.");
        }
        console.log(err);
      });
    handleCancel();
  }

  return (
    <Modal
      isOpen={isOpen}
      size="sm"
      centered
      toggle={handleCancel}
      onExit={() => console.log("modal closed")}
    >
      <ModalHeader toggle={handleCancel}>Rezervace</ModalHeader>
      <ModalBody>
        {"Přejete si přihlásit se na událost " + clickInfo?.event?.title + "?"}
      </ModalBody>

      <ModalFooter>
        {
          <Button color="secondary" onClick={handleSignUp}>
            Přihlásit
          </Button>
        }
        {
          <Button color="primary" onClick={handleCancel}>
            Zrušit
          </Button>
        }
      </ModalFooter>
    </Modal>
  );
};

export default SignUpModal;
