import React, { useState, useEffect } from "react";
import axios from "axios";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import { host, reservationsEndpoint } from "../../util/EndpointConfig";
import CustomGridLoader from "../CustomLoader";
import filterFactory, {
  textFilter,
  selectFilter,
} from "react-bootstrap-table2-filter";
import "react-bootstrap-table2-filter/dist/react-bootstrap-table2-filter.min.css";

const ReservationTable = () => {
  const [reservations, setReservations] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const canceledFormater = (data, row) => {
    console.log("zruseno " + data);
    return <>{data === false ? "Aktivní" : "Zrušena"}</>;
  };

  const dateFormatter = (data, row) => {
    return <>{data.substring(0, 16)}</>;
  };

  const columns = [
    { dataField: "reservationId", text: "ID" },
    {
      dataField: "owner.email",
      text: "Email",
      filter: textFilter({ placeholder: "Email zákazníka" }),
    },

    {
      dataField: "owner.fullName",
      filter: textFilter({ placeholder: "Jméno zákazníka" }),
      text: "Jméno",
    },
    { dataField: "eventId", text: "ID události" },
    {
      dataField: "title",
      text: "Událost",
      filter: textFilter({ placeholder: "Název události" }),
    },

    {
      dataField: "start",
      text: "Od",
      sort: true,
      formatter: dateFormatter,
    },
    {
      dataField: "end",
      text: "Do",
      formatter: dateFormatter,
    },
    {
      dataField: "isCanceled",
      text: "Stav",
      formatter: canceledFormater,
      filter: selectFilter({
        options: {
          true: "Zrušena",
          false: "Aktivní",
        },
        placeholder: "Stav",
        defaultValue: false,
      }),
    },
  ];

  useEffect(() => {
    axios
      .get(host + reservationsEndpoint, { timeout: 10000 })
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
      <div
        style={{
          //justifyContent: "center",
          //alignItems: "center",
          //display: "flex",
          paddingTop: 3,
          paddingLeft: 10,
          paddingRight: 10,
          //height: "100%",
          // background: "#c93060",
        }}
      >
        <BootstrapTable
          keyField="reservationId"
          columns={columns}
          data={reservations}
          striped
          // hover
          // condensed
          pagination={paginationFactory()}
          filter={filterFactory()}
        />
      </div>
    );
  }
};

export default ReservationTable;
