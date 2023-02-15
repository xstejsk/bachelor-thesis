import React, { useState, useEffect, useContext } from "react";
import ReservationTable from "./ReservationTable";
import {
  host,
  reservationsEndpoint,
  cancelMultipleReservationsEndpoint,
  activeReservationsByUser,
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

  const cancelReservations = (ids) => {
    axios
      .put(host + cancelMultipleReservationsEndpoint, { reservationIds: ids })
      .then((response) => {
        if (response.status === 200) {
          // fetchReservations();
          if (ids.length > 1) {
            alert.info("Rezervace byly zrušeny");
          } else if (ids.length == 1) {
            alert.info("Rezervace byla zrušena.");
          }

          fetchReservations();
        }
      })
      .catch((error) => {
        if (ids.length > 1) {
          alert.error("Rezervace nelze zrušit.");
        } else if (ids.length == 1) {
          alert.error("Rezervaci nelze zrušit.");
        }
      });
  };

  function fetchReservations() {
    let endpoint = host;
    if (globalState?.user?.role === "ROLE_USER") {
      endpoint += activeReservationsByUser.replace(
        "{userId}",
        globalState.user.userId
      );
    } else if (globalState?.user?.role === "ROLE_ADMIN") {
      endpoint += reservationsEndpoint;
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
  }

  useEffect(() => {
    let endpoint = host;
    if (globalState?.user?.role === "ROLE_USER") {
      endpoint += activeReservationsByUser.replace(
        "{userId}",
        globalState.user.userId
      );
    } else {
      endpoint += reservationsEndpoint;
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
      <div className="container-xxl">
        <ReservationTable
          reservations={reservations}
          cancelReservations={cancelReservations}
        />
      </div>
    );
  }
};
export default ReservationTableWrapper;
