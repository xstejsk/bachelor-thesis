import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-daterangepicker/daterangepicker.css";
import React, { useState } from "react";
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from "reactstrap";
import { Col, Row, Form } from "react-bootstrap";
import axios from "axios";
import { host, newLocationEndpoint } from "../../util/EndpointConfig";
import { useAlert } from "react-alert";

const NewLocationForm = ({ handleHide, isOpen, reloadLocations }) => {
  const [locationName, setLocationName] = useState("");
  const [locationExists, setLocationExists] = useState(false);
  const alert = useAlert();
  function handleCancel() {
    handleHide();
  }

  function handleSubmit() {
    const newLocation = {
      name: locationName,
    };

    axios
      .post(host + newLocationEndpoint, newLocation)
      .then((response) => {
        console.log("posting new location");
        if (response.status === 201) {
          reloadLocations();
          handleCancel();
          alert.success("Byl vytvořen nový kalendář");
        }
      })
      .catch((err) => {
        if (err.response.status === 409) {
          setLocationExists(true);
        }
      });
  }

  return (
    <Modal isOpen={isOpen} on backdrop="static" size="sm">
      <ModalHeader>Nové místo konání</ModalHeader>
      <ModalBody>
        <Form.Group className="mb-3" controlId="Name">
          <Row>
            <Col>
              <Form.Label className="text-center">Název</Form.Label>
              <Form.Control
                type="text"
                placeholder="Modrý sál"
                onChange={(e) => {
                  setLocationName(e.target.value);
                }}
                name="locationName"
                isInvalid={locationExists}
              />
              <Form.Control.Feedback type="invalid">
                Místo se stejným názvem již existuje
              </Form.Control.Feedback>
            </Col>
          </Row>
          <Row>
            <Col>
              <Form.Label className="text-center">Od</Form.Label>
              <Form.Control
                type="time"
                onChange={(e) => {
                  setLocationName(e.target.value);
                }}
                name="locationName"
              />
            </Col>

            <Col>
              <Form.Label className="text-center">Do</Form.Label>
              <Form.Control
                type="time"
                onChange={(e) => {
                  setLocationName(e.target.value);
                }}
                name="locationName"
              />
            </Col>
          </Row>
        </Form.Group>
      </ModalBody>

      <ModalFooter>
        {
          <Button color="secondary" onClick={handleCancel}>
            Zavřít
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

export default NewLocationForm;
