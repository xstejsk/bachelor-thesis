import React from "react";
import { Link } from "react-router-dom";

const NotFound = () => {
  return (
    <div
      style={{ display: "flex", justifyContent: "center", textAlign: "center" }}
    >
      <div className="container">
        <div className="row">
          <div className="col-md-6 mx-auto text-center">
            <h1>Jejda! 404</h1>
            <p>Tato stránka nebyla nalezena.</p>
            <Link to="/events" className="btn btn-primary">
              Zpět na úvod
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NotFound;
