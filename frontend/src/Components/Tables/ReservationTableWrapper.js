import React, { useState, useEffect, useContext } from "react";
import ReservationTable from "./ReservationTable";
import {
  host,
  reservationsEndpoint,
  reservationsByUser,
  deleteReservation,
} from "../../util/EndpointConfig";
import CustomGridLoader from "../CustomLoader";
import axios from "axios";
import { Context } from "../../util/GlobalState";
import { useAlert } from "react-alert";

const ReservationTableWrapper = () => {
  const [reservations, setReservations] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [globalState, setGlobalState] = useContext(Context);
  const alert = useAlert();

  const cancelReservation = (id) => {
    axios
      .delete(host + deleteReservation + id)
      .then((response) => {
        if (response.status === 200) {
          // fetchReservations();
          alert.success("Rezervace byla zrušena.");
          fetchReservations();
        }
      })
      .catch((error) => {
        alert.error("Rezervaci nelze zrušit.");
      });
  };

  function fetchReservations() {
    let endpoint = host;
    if (globalState?.user?.role === "ROLE_USER") {
      endpoint +=
        reservationsByUser.replace("{userId}", globalState.user.userId) +
        "?present=true";
    } else if (
      globalState?.user?.role === "ROLE_ADMIN" ||
      globalState?.user?.role === "ROLE_SUPER_ADMIN"
    ) {
      endpoint += reservationsEndpoint;
    }

    axios
      .get(endpoint, { timeout: 10000 })
      .then((response) => {
        setReservations(response.data);
      })
      .catch((err) => {
        console.log(err);
        setReservations([]);
        // handle timeout
      })
      .finally(() => setIsLoading(false));
  }

  useEffect(() => {
    let endpoint = host;
    if (globalState?.user?.role === "ROLE_USER") {
      endpoint +=
        reservationsByUser.replace("{userId}", globalState.user.userId) +
        "?present=true";
      console.log(endpoint);
    } else {
      endpoint += reservationsEndpoint;
    }

    axios
      .get(endpoint, { timeout: 10000 })
      .then((response) => {
        setReservations(response.data);
      })
      .catch((err) => {
        console.log(err);
        setReservations([]);
        // handle timeout
      })
      .finally(() => setIsLoading(false));
  }, []);
  if (isLoading) {
    return <CustomGridLoader isLoading={isLoading} />;
  } else {
    return (
      <div className="container-xxl">
        <ReservationTable
          reservations={reservations}
          cancelReservation={cancelReservation}
        />
      </div>
    );
  }
};
export default ReservationTableWrapper;
