import React, { useState, useEffect, useContext } from "react";
import ReservationTable from "./ReservationTable";
import {
  host,
  reservationsEndpoint,
  cancelMultipleReservationsEndpoint,
} from "../../util/EndpointConfig";
import CustomGridLoader from "../CustomLoader";
import axios from "axios";
import { Context } from "../../util/GlobalState";

const ReservationTableWrapper = () => {
  const [reservations, setReservations] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [globalState, setGlobalState] = useContext(Context);

  const cancelReservations = (ids) => {
    axios
      .put(host + cancelMultipleReservationsEndpoint, { reservationIds: ids })
      .then((response) => {
        if (response.status === 200) {
          let filteredReservations = reservations.filter((reservation) => {
            return !ids.includes(reservation.reservationId);
          });
          setReservations(filteredReservations);
          if (ids.length > 1) {
            alert("Rezervace byly zrušeny");
          } else if (ids.length == 1) {
            alert("Rezervace byla zrušena.");
          }

          console.log("reservations have been canceled");
        }
      })
      .catch((error) => {
        if (ids.length > 1) {
          alert("Události nelze zrušit.");
        } else if (ids.length == 1) {
          alert("Událost nelze zrušit.");
        }
      });
  };

  useEffect(() => {
    let endpoint = host + reservationsEndpoint;
    if (globalState?.user?.role === "ROLE_USER") {
      endpoint += "active/" + globalState.user.userId;
    } else if (globalState?.user?.role === "ROLE_ADMIN") {
      endpoint += "all";
    }

    axios
      .get(endpoint, { timeout: 10000 })
      .then((response) => {
        setReservations(response.data);
        console.log("reservations -----------");
        console.log(response.data);
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
      <div>
        <ReservationTable
          reservations={reservations}
          cancelReservations={cancelReservations}
        />
      </div>
    );
  }
};
export default ReservationTableWrapper;
