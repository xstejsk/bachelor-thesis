import React from "react";
import { useState, useEffect } from "react";
import { host, getAllUsersEndpoint } from "../../util/EndpointConfig";
import axios from "axios";
import CustomGridLoader from "../CustomLoader";
import UserTable from "./UserTable";

const UsersTableWrapper = () => {
  const [users, setUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    axios
      .get(host + getAllUsersEndpoint)
      .then((response) => {
        setUsers(response.data);
      })
      .catch((error) => {
        console.log(error.response.status);
      })
      .finally(() => setIsLoading(false));
  }, []);

  if (isLoading) {
    return <CustomGridLoader isLoading={isLoading} />;
  } else {
    return (
      <div className="container-xxl">
        <UserTable
          users={users}
          // cancelReservations={cancelReservations}
        />
      </div>
    );
  }
};

export default UsersTableWrapper;
