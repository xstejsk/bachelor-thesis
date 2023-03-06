import React, { useContext } from "react";
import axios from "axios";
import { host, logoutEndpoint } from "../../util/EndpointConfig";
import { Context } from "../../util/GlobalState";
import AdminNavbar from "./AdminNavbar";
import LoggedOutNavbar from "./LoggedOutNavbar";
import UserNavbar from "./UserNavbar";

const Navbar = () => {
  const [globalState, setGlobalState] = useContext(Context);
  const handleLogout = () => {
    axios
      .post(host + logoutEndpoint, {}, { withCredentials: true })
      .then((response) => {
        console.log(response.status);
        setGlobalState({ user: undefined });
        localStorage.clear();
      })
      .catch((err) => {
        console.log(err.response.status);
      });
  };
  if (globalState.user) {
    if (globalState.user.role === "ROLE_ADMIN") {
      return (
        <>
          <AdminNavbar handleLogout={handleLogout} />
        </>
      );
    } else {
      return (
        <>
          <UserNavbar handleLogout={handleLogout} />
        </>
      );
    }
  }
  return (
    <>
      <LoggedOutNavbar />
    </>
  );
};

export default Navbar;
