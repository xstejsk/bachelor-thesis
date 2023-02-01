import React, { useState, useContext, useEffect } from "react";
import axios from "axios";
import {
  host,
  loginEndpoint,
  resendEmailEndpoint,
  logoutEndpoint,
} from "../util/EndpointConfig";
import { useLocation, useNavigate } from "react-router-dom";
import { Context } from "../util/GlobalState";
import { Link } from "react-router-dom";
import { Col, Button, Row, Container, Form } from "react-bootstrap";
import { compareByFieldSpec } from "@fullcalendar/react";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const [globalState, setGlobalState] = useContext(Context);
  const [wrongPassword, setWrongPassword] = useState(false);

  useEffect(() => {
    setGlobalState({});
  }, []);

  const handleResendEmail = () => {
    axios
      .post(host + resendEmailEndpoint + username)
      .then((response) => {
        if (response.status === 200) {
          console.log("email has been resent");
          alert(
            "Váš účet je nutné aktivovat. Potvrzovací email byl znovu zaslán, zkontrolujte prosím vaši emailovou schránku."
          );
        }
      })
      .catch((err) => console.log(err));
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    axios
      .post(
        host + loginEndpoint,
        {
          username,
          password,
        },
        { withCredentials: true }
      )
      .then((response) => {
        if (response.status === 200) {
          console.log(response.data);
          // const accessToken = response.data["access_token"];
          // const refreshToken = response.data["refresh_token"];
          axios.defaults.headers.common[
            "Authorization"
          ] = `Bearer ${response.data["access_token"]}`;
          console.log(response.data["user"]);
          setGlobalState({ user: response.data["user"] });
          location.state?.from
            ? navigate(location.state.from)
            : navigate("/events");
        } else {
          alert("Zadaná kombinace emailu a hesla je nesprávná");
        }
      })
      .catch((err) => {
        if (err.response.status === 403) {
          handleResendEmail();
        } else if (err.response.status === 401) {
          setWrongPassword(true);
        }
      });
    //
  };

  return (
    <div>
      <Container>
        <Row className="d-flex justify-content-center align-items-center">
          <Col md={8} lg={6} xs={12}>
            <div className="mb-3 mt-md-4">
              <h2 className="fw-bold mb-2 text-center  ">Přihlašte se</h2>
              <div className="mb-3">
                <Form>
                  {wrongPassword && (
                    <div className="mt-3">
                      <p
                        style={{
                          color: "red",
                        }}
                        className="mb-0  text-center"
                      >
                        Neplatná kombinace emailu a hesla.
                      </p>

                      <p className="mb-0  text-center">
                        {" "}
                        Zapomenuté heslo?{" "}
                        <Link
                          to="/password/reset"
                          className="text-primary fw-bold"
                        >
                          Obnova hesla
                        </Link>
                      </p>
                    </div>
                  )}

                  <Form.Group className="mb-3" controlId="Name">
                    <Form.Label className="text-center">Email</Form.Label>
                    <Form.Control
                      type="email"
                      placeholder="Zadejte email"
                      onChange={(e) => {
                        setUsername(e.target.value);
                      }}
                    />

                    <Form.Label className="text-center">Heslo</Form.Label>
                    <Form.Control
                      type="password"
                      placeholder="Zadejte heslo"
                      onChange={(e) => {
                        setPassword(e.target.value);
                      }}
                    />
                  </Form.Group>
                  <div className="d-grid">
                    <Button
                      variant="primary"
                      type="submit"
                      onClick={handleLogin}
                    >
                      Přihlásit
                    </Button>
                  </div>
                </Form>

                <div className="mt-3">
                  <p className="mb-0  text-center">
                    Nemáte účet?{" "}
                    <Link to="/register" className="text-primary fw-bold">
                      Registrujte se
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

export default Login;
