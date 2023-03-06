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

const ReservationTable = ({ reservations, cancelReservation }) => {
  const [selected, setSelected] = useState(undefined);
  const [showDeleteReservationsModal, setShowDeleteReservationsModal] =
    useState(false);
  const [globalState, setGlobalState] = useContext(Context);

  const selectRow = {
    mode: "radio",
    clickToSelect: true,

    onSelect: (row, isSelect) => {
      if (isSelect) {
        console.log(row);
        setSelected(row);
      }
    },
  };

  const handleCancelReservation = () => {
    setShowDeleteReservationsModal(false);
    cancelReservation(selected.reservationId);
  };

  const handleCancelDelete = () => {
    setShowDeleteReservationsModal(false);
  };

  const dateFormatter = (data, row) => {
    return <>{data.substring(0, 16)}</>;
  };

  useEffect(() => {}, [selected]);

  const adminColumns = [
    {
      dataField: "reservationId",
      text: "ID rezervace",
      filter: textFilter({ placeholder: "1" }),
    },
    {
      dataField: "owner.email",
      text: "Email",
      filter: textFilter({ placeholder: "josef.halekal@seznam.cz" }),
    },

    {
      dataField: "owner.fullName",
      filter: textFilter({ placeholder: "Petr Pavel" }),
      text: "Jméno",
    },
    {
      dataField: "eventId",
      text: "ID události",
      filter: textFilter({ placeholder: "1" }),
    },
    {
      dataField: "title",
      text: "Událost",
      filter: textFilter({ placeholder: "tenis" }),
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

  const userColumns = [
    {
      dataField: "reservationId",
      text: "ID",
      filter: textFilter({ placeholder: "1" }),
    },
    {
      dataField: "eventId",
      text: "ID události",
      filter: textFilter({ placeholder: "1" }),
    },
    {
      dataField: "title",
      text: "Událost",
      filter: textFilter({ placeholder: "lekce jógy" }),
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

  let columns;
  if (globalState?.user?.role === "ROLE_ADMIN") {
    columns = adminColumns;
  } else {
    columns = userColumns;
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
      <ButtonGroup className="mb-3">
        <Button
          onClick={() => {
            setShowDeleteReservationsModal(true);
          }}
          variant="danger"
          disabled={selected === undefined}
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
        <ModalBody>{"Opravdu si přejete zrušit vybranou rezervaci?"}</ModalBody>
        <ModalFooter>
          <Button variant="secondary" onClick={handleCancelDelete}>
            Ne
          </Button>
          <Button variant="danger" onClick={handleCancelReservation}>
            Ano
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
};

export default ReservationTable;
