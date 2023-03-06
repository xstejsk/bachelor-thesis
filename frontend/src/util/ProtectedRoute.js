import React, { useState, useContext, useEffect } from "react";
import { Context } from "./GlobalState";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ children, requireAdmin }) => {
  const [globalState, setGlobalState] = useContext(Context);

  useEffect(() => {
    const user = localStorage.getItem("user");
    if (user) {
      setGlobalState({ user: JSON.parse(user) });
    }
  }, []);

  console.log("protected route");
  console.log(globalState);
  if (globalState.user === undefined) {
    console.log("not user");
    console.log(globalState);
    return <Navigate to="/login" replace />;
  } else if (requireAdmin && globalState.user?.role != "ROLE_ADMIN") {
    console.log("user role");
    return <Navigate to="/events" replace />;
  }
  return children;
};

export default ProtectedRoute;
