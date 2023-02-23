import React, { useState, useContext, useEffect } from "react";
import { Context } from "./GlobalState";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ children, requireAdmin }) => {
  const [globalState, setGlobalState] = useContext(Context);
  console.log("protected route");
  console.log(globalState);
  if (globalState.user === undefined) {
    console.log("not user");
    console.log(globalState);
    return <Navigate to="/login" replace />;
  } else if (requireAdmin && globalState.user?.role != "ROLE_ADMIN") {
    return <Navigate to="/events" replace />;
  }
  return children;
};

export default ProtectedRoute;
