import React from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import { host, logoutEndpoint } from "../../util/EndpointConfig";

const AdminNavbar = () => {
  const handleLogout = () => {
    axios
      .post(host + logoutEndpoint, {}, { withCredentials: true })
      .then((response) => {
        console.log(response.status);
      })
      .catch((err) => {
        console.log(err.response.status);
      });
  };

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
            <li>
              <Link to="/reservations" className="nav-link px-2 text-white">
                <i class="bi bi-book"></i> Rezervace
              </Link>
            </li>
            <li>
              <Link to="/users" className="nav-link px-2 text-white">
                <i class="bi bi-people"></i> Uživatelé
              </Link>
            </li>
          </ul>

          <div className="text-end">
            <Link
              to="/login"
              className="btn btn-warning"
              onClick={handleLogout}
            >
              <i class="bi bi-box-arrow-right"></i> Odhlásit
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
};
export default AdminNavbar;
