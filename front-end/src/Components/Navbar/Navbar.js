import React, { useContext } from "react";
import { Link } from "react-router-dom";
import { Context } from "../../util/GlobalState";
import AdminNavbar from "./AdminNavbar";
import LoggedOutNavbar from "./LoggedOutNavbar";
import UserNavbar from "./UserNavbar";

const Navbar = () => {
  const [globalState, setGlobalState] = useContext(Context);
  if (globalState.user) {
    if (globalState.user.role === "ROLE_ADMIN") {
      return (
        <>
          <AdminNavbar />
        </>
      );
    } else {
      return (
        <>
          <UserNavbar />
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
