import React from "react";
import { Link } from "react-router-dom";

const LoggedOutNavbar = () => {
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
                <i class="bi bi-calendar-event"></i> Události
              </Link>
            </li>
          </ul>

          <div className="text-end">
            <Link to="/login" className="btn btn-outline-light me-2">
              <i class="bi bi-box-arrow-in-right"></i> Přihlášení
            </Link>

            <Link to="/register" className="btn btn-warning">
              <i class="bi bi-pencil-square"></i> Registrace
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
};

export default LoggedOutNavbar;
