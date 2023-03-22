import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-daterangepicker/daterangepicker.css";
import React, { useEffect, useState } from "react";
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from "reactstrap";
import { Col, Row, Form } from "react-bootstrap";
import axios from "axios";
import { host, locationsEndpoint } from "../../util/EndpointConfig";
import { useAlert } from "react-alert";

const NewLocationForm = ({ handleHide, isOpen, reloadLocations }) => {
  const [newLocation, setNewLocation] = useState({
    name: "",
    opensAt: undefined,
    closesAt: undefined,
  });

  const [errors, setErrors] = useState({
    locationExists: false,
    closesBeforeOpens: false,
  })

  useEffect(() => {
    setNewLocation({
      name: "",
      opensAt: undefined,
      closesAt: undefined,
    })
    setErrors({
      locationExists: false,
      closesBeforeOpens: false,
    })
  },
  [isOpen])


  const handleUpdateLocation = (field, value) => {
    if(field === "opensAt") {
      if(value > newLocation.closesAt) {
        setErrors((prev) => ({ ...prev, ["closesBeforeOpens"]: true }));
      } else {
        setErrors((prev) => ({ ...prev, ["closesBeforeOpens"]: false }));
      }
    }
    else if(field === "closesAt") {
      if(value < newLocation.opensAt) {
        setErrors((prev) => ({ ...prev, ["closesBeforeOpens"]: true }));
      } else {
        setErrors((prev) => ({ ...prev, ["closesBeforeOpens"]: false }));
      }
    }
    if(field === "name" && errors.locationExists) {
      setErrors((prev) => ({ ...prev, ["locationExists"]: false }));
    }
    setNewLocation((prev) => ({ ...prev, [field]: value }));
  };

  
  const alert = useAlert();
  function handleCancel() {
    handleHide();
  }

  function handleSubmit() {
    axios
      .post(host + locationsEndpoint, newLocation)
      .then((response) => {
        if (response.status === 201) {
          reloadLocations();
          handleCancel();
          alert.success("Byl vytvořen nový kalendář");
        }
      })
      .catch((err) => {
        if (err.response.status === 409) {
          setErrors((prev) => ({ ...prev, ["locationExists"]: true }));
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
                  handleUpdateLocation(e.target.name, e.target.value);
                }}
                name="name"
                isInvalid={errors.locationExists}
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
                  handleUpdateLocation(e.target.name, e.target.value);
                }}
                name="opensAt"
              />
            </Col>

            <Col>
              <Form.Label className="text-center">Do</Form.Label>
              <Form.Control
                type="time"
                onChange={(e) => {
                  handleUpdateLocation(e.target.name, e.target.value);
                }}
                min={newLocation.opensAt}
                name="closesAt"
                isInvalid={errors.closesBeforeOpens}
              />
              <Form.Control.Feedback type="invalid">
                Nesmí být menší než "od"
              </Form.Control.Feedback>
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
          <Button color="primary" onClick={handleSubmit} disabled={errors.locationExists 
          || errors.closesBeforeOpens ||
          newLocation.closesAt === undefined ||
          newLocation.opensAt === undefined ||
          newLocation.name === undefined}>
            Uložit
          </Button>
        }
      </ModalFooter>
    </Modal>
  );
};

export default NewLocationForm;
