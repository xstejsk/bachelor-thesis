import React, { useEffect, useState } from "react";
import { Col, Button, Row, Container, Form } from "react-bootstrap";
import { Link } from "react-router-dom";
import axios from "axios";
import { forgotPasswordEndpoint, host } from "../../util/EndpointConfig";
import { useAlert } from "react-alert";

const ResetPassword = () => {
  const [email, setEmail] = useState("");
  const [submitButtonEnabled, setSubmitButtonEnabled] = useState(false);
  const alert = useAlert();

  const handlePasswordReset = () => {
    alert.info("Na uvedenou adresu bylo zasláno nové heslo.");
    axios.post(host + forgotPasswordEndpoint.replace("{email}", email));
  };

  useEffect(() => {
    setSubmitButtonEnabled(isEmail(email));
  }, [email]);

  function isEmail(email) {
    const re =
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
  }

  return (
    <div>
      <Container>
        <Row className="d-flex justify-content-center align-items-center">
          <Col md={8} lg={6} xs={12}>
            <div className="mb-3 mt-md-4">
              <h2 className="fw-bold mb-2 text-center  ">Reset hesla</h2>
              <div className="mb-3">
                <Form>
                  <Form.Group className="mb-3" controlId="Name">
                    <Form.Label className="text-center">Email</Form.Label>
                    <Form.Control
                      type="email"
                      placeholder="karel.basta@seznam.cz"
                      onChange={(e) => {
                        setEmail(e.target.value);
                      }}
                      isInvalid={!isEmail(email)}
                    />
                    <Form.Control.Feedback type="invalid">
                      Zadejte prosím platnou emailovou adresu
                    </Form.Control.Feedback>
                  </Form.Group>
                  <div className="d-grid">
                    <Button
                      variant="primary"
                      onClick={handlePasswordReset}
                      disabled={!submitButtonEnabled}
                    >
                      Zaslat nové heslo
                    </Button>
                  </div>
                  <Form.Group>
                    <Row>
                      <Col>
                        <p style={{ textAlign: "left" }} className="mt-4">
                          Máte účet?{" "}
                          <Link to="/login" className="text-primary fw-bold">
                            Přihlašte se
                          </Link>
                        </p>
                      </Col>
                      <Col>
                        <p style={{ textAlign: "right" }} className="mt-4">
                          Nemáte účet?{" "}
                          <Link to="/register" className="text-primary fw-bold">
                            Registrujte se
                          </Link>
                        </p>
                      </Col>
                    </Row>
                  </Form.Group>
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
