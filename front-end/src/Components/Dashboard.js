import axios from "axios";
import React, { useEffect, useState } from "react";
import { host, loggedInUserEndpoint } from "../util/EndpointConfig";

const Dashboard = () => {
  const [name, setName] = useState("");

  useEffect(() => {
    (async () => {
      await axios.get(host + loggedInUserEndpoint).then((response) => {
        if (response.status === 200) {
          setName(response.data.name);
        } else {
          setName("internal error");
        }
      });
    })();
  });

  return <div>Hello, {name}</div>;
};

export default Dashboard;
