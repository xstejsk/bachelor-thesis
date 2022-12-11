import React from "react";
import { Link } from "react-router-dom";

const UserNavbar = () => {
  return (
    <header className="p-3 text-bg-dark">
      <div className="container">
        <div className="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
          <a
            href="/"
            className="d-flex align-items-center mb-2 mb-lg-0 text-white text-decoration-none"
          ></a>

          <ul className="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0">
            <li>
              <Link to="/events" className="nav-link px-2 text-white">
                Události
              </Link>
            </li>
            <li>
              <Link to="/reservations" className="nav-link px-2 text-white">
                Moje rezervace
              </Link>
            </li>
          </ul>

          <div className="text-end">
            <Link to="/login" className="btn btn-warning">
              {console.log("logout")}
              Odhlásit
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
};

export default UserNavbar;
