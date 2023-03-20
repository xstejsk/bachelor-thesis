import axios from "axios";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { host, submitConfirmationsTokenEndpoint } from "../util/EndpointConfig";
import success from "../resources/check128.png";
import fail from "../resources/error128.png";
import Card from "react-bootstrap/Card";
import { Container } from "react-bootstrap";
import { Link } from "react-router-dom";

const ConfirmRegistration = () => {
  const search = useLocation().search;
  const token = new URLSearchParams(search).get("token");
  const [status, setStatus] = useState({ icon: {}, message: "" });

  useEffect(() => {
    axios
      .put(host + submitConfirmationsTokenEndpoint, {token: token})
      .then((response) => {
        setStatus({
          icon: success,
          message: "Registrace proběhla úspěšně, nyní se můžete přihlásit.",
        });
      })
      .catch((error) => {
        if (error.response.status === 409) {
          // already confirmed
          setStatus({
            icon: success,
            message: "Účet již byl aktivován, prosím, přihlašte se.",
          });
        } else if (error.response.status === 410) {
          // expired
          setStatus({
            icon: fail,
            message:
              "Doba platnosti odkazu již vypršela, při dalším pokusu o přihlášení Vám bude zaslán nový odkaz.",
          });
        } else {
          setStatus({ icon: fail, message: "Tento odkaz je neplatný." });
        }
      });
  }, [token]);

  return (
    <Container className="d-flex mt-4 justify-content-center align-items-center">
      <Card
        style={{ width: "23rem" }}
        className=" shadow p-3 mb-5 bg-white rounded"
      >
        <Container className="d-flex mt-4 justify-content-center align-items-center">
          <Card.Img
            variant="top"
            src={status.icon}
            style={{ width: "70px", height: "70px" }}
          />
        </Container>

        <Card.Body>
          <Card.Title>Potvrzení registrace</Card.Title>
          <Card.Text>{status.message}</Card.Text>
          <Link
            className="btn btn-primary"
            style={{ width: "100%" }}
            to="/login"
          >
            Přihlašte se{" "}
          </Link>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default ConfirmRegistration;
