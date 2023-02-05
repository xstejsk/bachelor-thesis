import React, { useState, useEffect, useContext } from "react";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import filterFactory, {
  textFilter,
  selectFilter,
} from "react-bootstrap-table2-filter";
import "react-bootstrap-table2-filter/dist/react-bootstrap-table2-filter.min.css";
import Button from "react-bootstrap/Button";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import ButtonToolbar from "react-bootstrap/ButtonToolbar";
import { Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";
import { Context } from "../../util/GlobalState";

const ReservationTable = ({ reservations, cancelReservations }) => {
  const [selected, setSelected] = useState([]);
  const [showDeleteReservationsModal, setShowDeleteReservationsModal] =
    useState(false);
  const [globalState, setGlobalState] = useContext(Context);

  const canceledFormater = (data, row) => {
    console.log("zruseno " + data);
    return <>{data === false ? "Aktivní" : "Zrušena"}</>;
  };

  const handleSelectAll = (isSelected, rows) => {
    if (isSelected) {
      setSelected(rows.map((row) => row.reservationId));
    } else {
      setSelected([]);
    }
  };
  const handleCancelReservations = () => {
    setShowDeleteReservationsModal(false);
    cancelReservations(selected);
  };

  const handleCancelDelete = () => {
    setShowDeleteReservationsModal(false);
  };

  const dateFormatter = (data, row) => {
    return <>{data.substring(0, 16)}</>;
  };

  useEffect(() => {
    console.log("reservation table use effect ran");
  }, [selected]);

  const selectRow = {
    mode: "checkbox",
    clickToSelect: true,
    onSelect: (row, isSelect) => {
      if (isSelect) {
        console.log(row);
        setSelected((prevSelected) => [...prevSelected, row.reservationId]);
      } else {
        setSelected((prevSelected) =>
          prevSelected.filter((item) => item !== row.reservationId)
        );
      }
    },
    onSelectAll: handleSelectAll,
  };

  const columns = [
    {
      dataField: "reservationId",
      text: "ID",
      filter: textFilter({ placeholder: "ID rezervace" }),
    },
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
    {
      dataField: "eventId",
      text: "ID události",
      filter: textFilter({ placeholder: "ID události" }),
    },
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
  ];

  if (globalState?.user?.role === "ROLE_ADMIN") {
    columns.push({
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
    });
  }

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
      <ButtonGroup>
        <Button
          onClick={() => {
            console.log("hello");
          }}
          variant="secondary"
        >
          Nová rezervace
        </Button>
        <Button
          onClick={() => {
            console.log(selected.length);
            setShowDeleteReservationsModal(selected.length > 0);
          }}
          variant="danger"
        >
          Zrušit vybrané
        </Button>
      </ButtonGroup>
      <BootstrapTable
        keyField="reservationId"
        columns={columns}
        data={reservations}
        striped
        // hover
        // condensed
        pagination={paginationFactory()}
        filter={filterFactory()}
        selectRow={selectRow}
      />

      <Modal
        isOpen={showDeleteReservationsModal}
        on
        backdrop="static"
        size="sm"
        centered={true}
      >
        <ModalHeader>Zrušit rezervace</ModalHeader>
        <ModalBody>{"Opravdu si přejete zrušit vybrané rezervace?"}</ModalBody>
        <ModalFooter>
          <Button variant="secondary" onClick={handleCancelDelete}>
            Ne
          </Button>
          <Button variant="danger" onClick={handleCancelReservations}>
            Ano
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
};

export default ReservationTable;
