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

const UserTable = ({ users }) => {
  const roleFormatter = (data, row) => {
    return <>{data === "ROLE_ADMIN" ? "Admin" : "Uživatel"}</>;
  };

  const booleanFormater = (data, row) => {
    return <>{data === false ? "Ne" : "Ano"}</>;
  };

  const columns = [
    {
      dataField: "userId",
      text: "ID uživatele",
      filter: textFilter({ placeholder: "1" }),
    },
    {
      dataField: "fullName",
      text: "Celé jméno",
      filter: textFilter({ placeholder: "Čestmír Strakatý" }),
    },

    {
      dataField: "email",
      filter: textFilter({ placeholder: "cesmitr.strakaty@gmail.com" }),
      text: "Email",
    },
    {
      dataField: "enabled",
      text: "Aktivovaný",
      formatter: booleanFormater,
      filter: selectFilter({
        options: {
          true: "Ano",
          false: "Ne",
        },
        placeholder: "Vyberte stav",
        defaultValue: false,
      }),
    },
    {
      dataField: "locked",
      text: "Zablokovaný",
      formatter: booleanFormater,
      filter: selectFilter({
        options: {
          true: "Ano",
          false: "Ne",
        },
        placeholder: "Vyberte stav",
        defaultValue: false,
      }),
    },
    {
      dataField: "role",
      text: "Role uživatele",
      formatter: roleFormatter,
      filter: selectFilter({
        options: {
          ROLE_ADMIN: "Admin",
          ROLE_USER: "Uživatel",
        },
        placeholder: "Uživatel",
        defaultValue: false,
      }),
    },
  ];

  return (
    <div
      style={{
        paddingTop: 3,
        paddingLeft: 10,
        paddingRight: 10,
      }}
    >
      <ButtonGroup className="mb-3">
        <Button
          onClick={() => {
            //console.log(selected.length);
            // setShowDeleteReservationsModal(selected.length > 0);
          }}
          variant="danger"
        >
          Zrušit vybrané
        </Button>
      </ButtonGroup>
      <BootstrapTable
        keyField="reservationId"
        columns={columns}
        data={users}
        striped
        // hover
        // condensed
        pagination={paginationFactory()}
        filter={filterFactory()}
        //selectRow={selectRow}
      />

      {/* <Modal
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
          </Modal> */}
    </div>
  );
};

export default UserTable;
