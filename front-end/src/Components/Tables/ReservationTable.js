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

  useEffect(() => {}, [selected]);

  const selectRow = {
    mode: "checkbox",
    clickToSelect: true,
    onSelect: (row, isSelect) => {
      if (isSelect) {
        setSelected((prevSelected) => [...prevSelected, row.reservationId]);
      } else {
        setSelected((prevSelected) =>
          prevSelected.filter((item) => item !== row.reservationId)
        );
      }
    },
    onSelectAll: handleSelectAll,
  };

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
