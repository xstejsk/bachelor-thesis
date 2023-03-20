import React, { useContext } from "react";
import { Context } from "../../util/GlobalState";
import AdminNavbar from "./AdminNavbar";
import LoggedOutNavbar from "./LoggedOutNavbar";
import UserNavbar from "./UserNavbar";

const Navbar = () => {
  const [globalState, setGlobalState] = useContext(Context);
  const handleLogout = () => {
    localStorage.clear();
    setGlobalState({})
  };

  if (globalState.user) {
    if (
      globalState.user.role === "ROLE_ADMIN" ||
      globalState?.user?.role === "ROLE_SUPER_ADMIN"
    ) {
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
