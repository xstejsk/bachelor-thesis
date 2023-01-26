import React, { useState } from "react";
import { Col, Button, Row, Container, Form } from "react-bootstrap";
import axios from "axios";
import { forgotPasswordEndpoint, host } from "../../util/EndpointConfig";

const ResetPassword = () => {
  const [email, setEmail] = useState("");
  const [showMessage, setShowMessage] = useState(false);

  const handlePasswordReset = () => {
    axios.post(host + forgotPasswordEndpoint.replace("{email}", email));
    setShowMessage(true);
  };

  return (
    <div>
      <Container>
        <Row className="d-flex justify-content-center align-items-center">
          <Col md={8} lg={6} xs={12}>
            <div className="mb-3 mt-md-4">
              <h2 className="fw-bold mb-2 text-center  ">Reset hesla</h2>
              <div className="mb-3">
                <Form>
                  {showMessage && (
                    <div className="mt-3">
                      <p
                        style={{
                          color: "green",
                          fontWeight: "bold",
                        }}
                        className="mb-0  text-center"
                      >
                        Vaše nové heslo bylo zasláno na uvedenou emailovou
                        adresu, pokud je adresa platná.
                      </p>
                    </div>
                  )}

                  <Form.Group className="mb-3" controlId="Name">
                    <Form.Label className="text-center">Email</Form.Label>
                    <Form.Control
                      type="email"
                      placeholder="Zadejte email"
                      onChange={(e) => {
                        setEmail(e.target.value);
                      }}
                    />
                  </Form.Group>
                  <div className="d-grid">
                    <Button variant="primary" onClick={handlePasswordReset}>
                      Zaslat nové heslo
                    </Button>
                  </div>
                </Form>
              </div>
            </div>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default ResetPassword;
