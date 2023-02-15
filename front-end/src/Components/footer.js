import React from "react";
import { Row, Col } from "reactstrap";

const Footer = () => {
  return (
    <footer className=" pt-2 pb-4">
      <div className="container text-center text-md-left">
        <div className="row text-center text-md-left">
          <Row>
            <Col className="col-md-3 col-lg-3 col-xl-3 mx-auto mt-3">
              <h5 className="text-uppercase mb-4 font-weight-bold ">
                Reservoir s.r.o.
              </h5>

              <p>Nejlepší rezervační systém v Angoře</p>
            </Col>
          </Row>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
