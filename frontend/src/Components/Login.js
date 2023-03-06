import React, { useState, useContext, useEffect } from "react";
import axios from "axios";
import {
  host,
  loginEndpoint,
  resendEmailEndpoint,
} from "../util/EndpointConfig";
import { useLocation, useNavigate } from "react-router-dom";
import { Context } from "../util/GlobalState";
import { Link } from "react-router-dom";
import { Col, Button, Row, Container, Form } from "react-bootstrap";
import { useAlert } from "react-alert";
import { Navigate } from "react-router-dom";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const [globalState, setGlobalState] = useContext(Context);
  const alert = useAlert();
  const [submitButtonEnabled, setSubmitButtonEnabled] = useState(false);

  // useEffect(() => {

  //   // setGlobalState({});
  // }, []);

  useEffect(() => {
    setSubmitButtonEnabled(password != "" && username != "");
  }, [username, password]);

  const handleResendEmail = () => {
    axios
      .post(host + resendEmailEndpoint.replace("{emailAddress}", username))
      .then((response) => {
        if (response.status === 200) {
          console.log("email has been resent");
          alert.info(
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
          localStorage.setItem("user", JSON.stringify(response.data["user"]));
          localStorage.setItem(
            "access_token",
            JSON.stringify(response.data["access_token"])
          );
          localStorage.setItem(
            "refresh_token",
            JSON.stringify(response.data["refresh_token"])
          );
          location.state?.from
            ? navigate(location.state.from)
            : navigate("/events");
        }
      })
      .catch((err) => {
        if (err.response.status === 403) {
          if (err.response.data.includes("verified")) {
            handleResendEmail();
          } else if (err.response.data.includes("locked")) {
            alert.error("Účet byl zablokován, prosím, kontaktujte správce");
          } else {
            alert.error("Zadaná kombinace emailu a hesla je nesprávná");
          }
        } else if (err.response.status === 401) {
          alert.error("Zadaná kombinace emailu a hesla je nesprávná");
        }
      });
    //
  };
  // if (localStorage.getItem("user")) {
  //   return <Navigate to="/events" replace />;
  // } else {
  return (
    <div>
      <Container>
        <Row className="d-flex justify-content-center align-items-center">
          <Col md={8} lg={6} xs={12}>
            <div className="mb-3 mt-md-4">
              <h2 className="fw-bold mb-2 text-center">Přihlašte se</h2>
              <div className="mb-3">
                <Form>
                  <Form.Group className="mb-3" controlId="Name">
                    <Form.Label className="text-center">Email</Form.Label>
                    <Form.Control
                      type="email"
                      placeholder="karel.divis@gmail.com"
                      onChange={(e) => {
                        setUsername(e.target.value);
                      }}
                    />

                    <Form.Label className="text-left">Heslo</Form.Label>
                    <Form.Control
                      type="password"
                      placeholder=""
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
                      disabled={!submitButtonEnabled}
                    >
                      Přihlásit
                    </Button>
                  </div>
                </Form>
              </div>
            </div>
            <Form.Group>
              <Row>
                <Col>
                  <p style={{ textAlign: "left" }} className="mt-1">
                    Nemáte účet?{" "}
                    <Link to="/register" className="text-primary fw-bold">
                      Registrujte se
                    </Link>
                  </p>
                </Col>
                <Col>
                  <p style={{ textAlign: "right" }} className="mt-1">
                    Zapomenuté heslo?{" "}
                    <Link to="/password/reset" className="text-primary fw-bold">
                      Obnova hesla
                    </Link>
                  </p>
                </Col>
              </Row>
            </Form.Group>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default Login;
