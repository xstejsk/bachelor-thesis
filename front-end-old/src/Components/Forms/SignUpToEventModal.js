import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-daterangepicker/daterangepicker.css";

import React, { useState } from "react";
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from "reactstrap";
// import createReactClass from "create-react-class";

const SignUpModal = ({ clickInfo, isOpen, setIsOpen }) => {
  function handleCancel() {
    setIsOpen(false);
  }

  function handleSignUp() {
    console.log("prihlasit se");
    handleCancel();
  }

  return (
    <Modal isOpen={isOpen} size="sm" centered toggle={handleCancel}>
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
