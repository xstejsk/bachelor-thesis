import React from "react";
import { useState } from "react";
import { Col, Button, Row, Container, Card, Form } from "react-bootstrap";
import { Link } from "react-router-dom";
import axios from "axios";
import {
  host,
  registerEndpoint,
  resendEmailEndpoint,
} from "../../util/EndpointConfig";

const Register = () => {
  const [newUser, setNewUser] = useState({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
  });

  const [errors, setErrors] = useState({
    email: "Zadejte prosím platnou emailovou adresu",
    password: "Heslo musí mít alespoň 8 znaků",
    firstName: "Zadejte prosím své jméno",
    lastName: "Zadejte prosím své příjmení",
  });

  const [message, setMessage] = useState({ text: "", color: "" });
  const [submitButtonEnabled, setSubmitButtonEnabled] = useState(false);
  const [controlPassword, setControlPassword] = useState("");

  const handleResendEmail = () => {
    axios
      .post(host + resendEmailEndpoint + newUser.email)
      .then((response) => {
        if (response.status === 200) {
          console.log("email has been resent");
          alert(
            "Potvrzovací email byl znovu zaslán, zkontrolujte prosím vaši emailovou schránku."
          );
        }
      })
      .catch((err) => console.log(err));
  };

  const handleRegistration = () => {
    axios
      .post(host + registerEndpoint, newUser)
      .then((response) => {
        if (response.status === 201) {
          console.log("sucess reg");
          setMessage({
            text: "Na uvedenou adresu byl zaslán potvrzovací email, po ověření se můžete přihlásit",
            color: "green",
          });
        } else {
          console.log(response.status);
        }
      })
      .catch((error) => {
        console.log(error.response.status);

        if (error.response.status == 409) {
          setMessage({
            text: "Účet s touto emailovou adresou již existuje, prosím, přihlašte se",
            color: "red",
          });
        }
      });
  };

  const handleChange = (field, value) => {
    let errorMessage = "";
    if (field === "password") {
      if (value.length < 8) {
        errorMessage = "Heslo musí mít alespoň 8 znaků";
      }
    } else if (field === "email") {
      if (!isEmail(value)) {
        errorMessage = "Zadejte prosím platnou emailovou adresu";
      }
    } else if (field === "firstName" || field === "lastName") {
      if (value.length === 0) {
        errorMessage = `Zadejte prosím své ${
          field === "firstName" ? "jméno" : "příjmení"
        }`;
      }
    }
    setErrors((prev) => ({ ...prev, [field]: errorMessage }));
    setNewUser((prev) => ({ ...prev, [field]: value }));
    setSubmitButtonEnabled(
      !(
        errors.email !== "" ||
        errors.password !== "" ||
        errors.firstName !== "" ||
        errors.lastName !== ""
      ) && newUser.password === controlPassword
    );
    console.log(
      !(
        errors.email !== "" ||
        errors.password !== "" ||
        errors.firstName !== "" ||
        errors.lastName !== ""
      )
    );
    console.log(newUser.password === controlPassword);
  };

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
              <h2 className="fw-bold mb-2 text-center  ">Registrace</h2>
              <div className="mb-3">
                <Form>
                  {message.color == "green" && (
                    <div className="mt-3">
                      <p
                        style={{
                          color: message.color,
                          fontWeight: "bold",
                        }}
                        className="mb-0  text-center"
                      >
                        {message.text}

                        <Button
                          variant="success"
                          //type="submit"
                          onClick={handleResendEmail}
                        >
                          Poslat znovu
                        </Button>
                      </p>
                    </div>
                  )}
                  <Form.Group className="mb-3" controlId="Name">
                    <Row>
                      <Col>
                        <Form.Label className="text-center">Jméno</Form.Label>
                        <Form.Control
                          type="text"
                          placeholder="zadejte jméno"
                          onChange={(e) => {
                            handleChange(e.target.name, e.target.value);
                          }}
                          name="firstName"
                          isInvalid={newUser.firstName === ""}
                        />
                        <Form.Control.Feedback type="invalid">
                          {errors.firstName}
                        </Form.Control.Feedback>
                      </Col>
                      <Col>
                        <Form.Label className="text-center">
                          Příjmení
                        </Form.Label>
                        <Form.Control
                          type="text"
                          placeholder="zadejte příjmení"
                          onChange={(e) => {
                            handleChange(e.target.name, e.target.value);
                          }}
                          name="lastName"
                          isInvalid={newUser.lastName === ""}
                        />
                        <Form.Control.Feedback type="invalid">
                          {errors.lastName}
                        </Form.Control.Feedback>
                      </Col>
                    </Row>
                  </Form.Group>

                  <Form.Group className="mb-3" controlId="formBasicEmail">
                    <Form.Label className="text-center">Email</Form.Label>
                    <Form.Control
                      type="email"
                      placeholder="zadejte email"
                      onChange={(e) => {
                        handleChange(e.target.name, e.target.value);
                      }}
                      name="email"
                      isInvalid={!isEmail(newUser.email)}
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.email}
                    </Form.Control.Feedback>
                  </Form.Group>

                  <Form.Group className="mb-3" controlId="formBasicPassword">
                    <Form.Label>Heslo</Form.Label>
                    <Form.Control
                      type="password"
                      placeholder="zadejte heslo"
                      onChange={(e) => {
                        handleChange(e.target.name, e.target.value);
                      }}
                      name="password"
                      isInvalid={newUser.password.length < 8}
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.password}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <Form.Group className="mb-3" controlId="formBasicPassword">
                    <Form.Label>Heslo pro kontrolu</Form.Label>
                    <Form.Control
                      type="password"
                      placeholder="heslo znovu"
                      isInvalid={newUser.password !== controlPassword}
                      onChange={(e) => {
                        setControlPassword(e.target.value);
                        setSubmitButtonEnabled(
                          !(
                            errors.email !== "" ||
                            errors.password !== "" ||
                            errors.firstName !== "" ||
                            errors.lastName !== ""
                          ) && newUser.password === e.target.value
                        );
                      }}
                    />
                    <Form.Control.Feedback type="invalid">
                      Hesla se neshodují
                    </Form.Control.Feedback>
                  </Form.Group>
                  <Form.Group
                    className="mb-3"
                    controlId="formBasicCheckbox"
                  ></Form.Group>
                  <div className="d-grid">
                    <Button
                      variant="primary"
                      //type="submit"
                      disabled={!submitButtonEnabled}
                      onClick={handleRegistration}
                    >
                      Vytvořit účet
                    </Button>
                  </div>
                </Form>
                <div className="mt-3">
                  <p className="mb-0  text-center">
                    Již máte účet?{" "}
                    <Link to="/login" className="text-primary fw-bold">
                      Přihlašte se
                    </Link>
                  </p>
                </div>
              </div>
            </div>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default Register;
