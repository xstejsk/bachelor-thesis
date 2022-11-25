import React from "react";
import { useLocalState } from "./util/LocalStorageUtil";
import { Navigate, Outlet, useLocation } from "react-router-dom";
import Login from "./Components/Login";

const UserAuth = () => {
  // const [jwt, setJwt] = useLocalState("", "jwt"); // TODO: validate this on server
  // return jwt !== "";
  return true;
};

const PrivateRoute = () => {
  const location = useLocation();
  const isAuth = UserAuth();
  if (isAuth) {
    console.log("auth");
  } else {
    console.log("noauth");
  }
  return isAuth ? (
    <Outlet />
  ) : (
    <Navigate to="/login" replace state={{ from: location }} />
  );
};

export default PrivateRoute;
