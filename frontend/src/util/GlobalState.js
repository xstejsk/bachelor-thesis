import React, { useEffect, useState } from "react";
import Cookies from "universal-cookie";
import { host, refreshTokenEndpoint } from "./EndpointConfig";
import axios from "axios";
import { Navigate } from "react-router-dom";
const initialState = { user: undefined };

export const Context = React.createContext();

const GlobalState = ({ children }) => {
  const cookies = new Cookies();
  const [globalState, setGlobalState] = useState(initialState);
  // useEffect(() => {
  //   if (localStorage.getItem("user")) {
  //     const user = JSON.parse(localStorage.getItem("user"));
  //     const accessToken = JSON.parse(localStorage.getItem("access_token"));
  //     if (accessToken) {
  //       setGlobalState({ user: user });
  //       axios.defaults.headers.common[
  //         "Authorization"
  //       ] = `Bearer ${accessToken}`;
  //     } else {
  //       console.log("no refresh token in local storage");
  //     }
  //   } else {
  //     console.log("no user in global storage");
  //   }
  //   console.log("reloaded global state");
  // }, []);
  return (
    <Context.Provider value={[globalState, setGlobalState]}>
      {children}
    </Context.Provider>
  );
};

export default GlobalState;
