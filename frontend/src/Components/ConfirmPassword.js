import axios from "axios";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { host, resetPasswordEdnpoint } from "../util/EndpointConfig";
import Card from "react-bootstrap/Card";
import { Container } from "react-bootstrap";
import { Link } from "react-router-dom";
import success from "../resources/check128.png";
import fail from "../resources/error128.png";

const ConfirmPassword = () => {
  const search = useLocation().search;
  const token = new URLSearchParams(search).get("token");
  const [status, setStatus] = useState({ icon: {}, message: "" });

  useEffect(() => {
    axios
      .put(host + resetPasswordEdnpoint + token)
      .then((response) => {
        if (response.status === 200) {
          setStatus({
            icon: success,
            message: "Heslo bylo úspěšně změněno, nyní se můžete přihlásit.",
          });
        }
      })
      .catch((error) => {
        if (error.response.status === 409) {
          // already confirmed
          setStatus({
            icon: success,
            message: "Vaše heslo bylo již změněno, přihlašte se prosím.",
          });
        } else if (error.response.status === 410) {
          // expired
          setStatus({
            icon: fail,
            message:
              "Heslo není možné změnit, protože tento odkaz již není platný.",
          });
        } else {
          console.log(error.response.status);
          setStatus({
            icon: fail,
            message:
              "Heslo nebylo možné změnit, přihlašte se nebo Vaše heslo obnovte znovu.",
          });
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
          <Card.Title>Reset hesla</Card.Title>
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

export default ConfirmPassword;
