import React, { useState, useEffect } from "react";
import axios from "axios";
import { Table, Row, Col } from "reactstrap";
import { host, mockReservationsEndpoint } from "../../util/EndpointConfig";
import CustomGridLoader from "../CustomLoader";
import BootstrapTable from "react-bootstrap-table-next";
import "bootstrap/dist/css/bootstrap.min.css";

const DataList = () => {
  const [reservations, setReservations] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const columns = [
    { dataField: "reservationId", text: "ID" },
    { dataField: "owner.email", text: "Email vlastníka" },
    { dataField: "owner.firstName", text: "Jméno" },
    { dataField: "owner.lastName", text: "Příjmení" },
    { dataField: "eventId", text: "ID události" },
    { dataField: "title", text: "Název události", sort: true },
    { dataField: "start", text: "Od", sort: true },
    { dataField: "end", text: "Do", sort: true },
    { dataField: "canceled", text: "Zrušeno" },
  ];

  useEffect(() => {
    axios
      .get(host + mockReservationsEndpoint, { timeout: 10000 })
      .then((response) => {
        setReservations(response.data);
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
      <BootstrapTable
        keyField="reservationId"
        columns={columns}
        data={reservations}
      />

      //   <Table>
      //     <thead>
      //       <tr>
      //         <th>ID</th>
      //         <th>Email vlastníka</th>
      //         <th>Jméno vlastníka</th>
      //         <th>ID události</th>
      //         <th>Název události</th>
      //         <th>Od</th>
      //         <th>Do</th>
      //         <th>Zrušeno</th>
      //       </tr>
      //     </thead>
      //     <tbody>
      //       {reservations &&
      //         reservations.map((reservation) => (
      //           <tr>
      //             <td>{new String(reservation.reservationId)}</td>
      //             <td>{reservation.owner["email"]}</td>
      //             <td>
      //               {reservation.owner["firstName"] +
      //                 reservation.owner["secondName"]}
      //             </td>
      //             <td>{reservation["eventId"]}</td>
      //             <td>{reservation["title"]}</td>
      //             <td>{reservation["start"]}</td>
      //             <td>{reservation["end"]}</td>
      //             <td>{reservation["canceled"] ? "Ano" : "Ne"}</td>
      //           </tr>
      //         ))}
      //     </tbody>
      //   </Table>
    );
  }
};

export default DataList;
