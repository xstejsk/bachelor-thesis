import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-daterangepicker/daterangepicker.css";
import React, { useEffect, useState } from "react";
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from "reactstrap";
import { Col, Row, Container, Card, Form } from "react-bootstrap";
import axios from "axios";
import { host, newLocationEndpoint } from "../../util/EndpointConfig";

const NewLocationForm = ({ handleHide, isOpen, addLocation }) => {
  const [locationName, setLocationName] = useState("");
  const [locationExists, setLocationExists] = useState(false);
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
          addLocation(response.data);
          handleCancel();
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
        </Form.Group>
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

export default NewLocationForm;
