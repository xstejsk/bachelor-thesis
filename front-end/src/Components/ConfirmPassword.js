import axios from "axios";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { host, resetPasswordEdnpoint } from "../util/EndpointConfig";

const ConfirmPassword = () => {
  const search = useLocation().search;
  const token = new URLSearchParams(search).get("token");
  const [message, setMessage] = useState("");

  useEffect(() => {
    axios
      .put(host + resetPasswordEdnpoint + token)
      .then((response) => {
        if (response.status === 200) {
          setMessage("heslo bylo změněno");
        } else if (response.status === 409) {
          // already confirmed
          setMessage("účet je již aktivován");
        } else if (response.status === 410) {
          // expired
          setMessage("token již není platný");
        } else {
          setMessage("neplatný token");
        }
      })
      .catch((err) => setMessage("chyba"));
  }, []);

  return <div>Message: {message}</div>;
};

export default ConfirmPassword;
