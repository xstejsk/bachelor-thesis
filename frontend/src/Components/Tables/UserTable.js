import React, { useState, useContext } from "react";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import { Context } from "../../util/GlobalState";
import filterFactory, {
  textFilter,
  selectFilter,
} from "react-bootstrap-table2-filter";
import "react-bootstrap-table2-filter/dist/react-bootstrap-table2-filter.min.css";
import Button from "react-bootstrap/Button";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import { Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";
import axios from "axios";
import {
  host,
  promoteUser,
  banUser,
  unbanUser,
  deleteUser,
} from "../../util/EndpointConfig";
import { useAlert } from "react-alert";

const UserTable = ({ users, reloadUsers }) => {
  const alert = useAlert();
  const [isBanModalOpen, setIsBanModalOpen] = useState(false);
  const [isUnbanModalOpen, setIsUnbanModalOpen] = useState(false);
  const [isPromoteModalOpen, setIsPromoteModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState(0);
  const [selectedRow, setSelectedRow] = useState(null);
  const [globalState, setGlobalState] = useContext(Context);

  const selectRow = {
    mode: "radio",
    clickToSelect: true,

    onSelect: (row, isSelect) => {
      if (isSelect) {
        console.log(row);
        setSelectedRow(row);
      }
    },
  };

  const roleFormatter = (data, row) => {
    if (data === "ROLE_ADMIN") {
      return "Admin";
    } else if (data === "ROLE_SUPER_ADMIN") {
      return "Superadmin";
    } else {
      return "Uživatel";
    }
  };

  const booleanFormater = (data, row) => {
    return <>{data === false ? "Ne" : "Ano"}</>;
  };

  const roleOptions = [
    { value: "ROLE_ADMIN", label: "Admin" },
    { value: "ROLE_USER", label: "Uživatel" },
    { value: "ROLE_SUPER_ADMIN", label: "Superdmin" },
  ];

  const booleanOptions = [
    { value: "true", label: "Ano" },
    { value: "false", label: "Ne" },
  ];

  const handlePromoteUser = () => {
    console.log(selectedUserId);
    axios
      .put(host + promoteUser + selectedRow?.userId)
      .then((response) => {
        if (response.status === 200) {
          alert.success("Uživatel byl povýšen na administrátora");
          reloadUsers();
        }
      })
      .catch((error) => {
        console.log(error.response.status);
        alert.error("Uživatel nemohl být povýšen na administrátora");
      })
      .finally(() => {
        reloadUsers();
        hidePromoteModal();
      });
  };
  const handleBanUser = () => {
    axios
      .put(host + banUser + selectedRow?.userId)
      .then((response) => {
        if (response.status === 200) {
          alert.info("Uživatel byl zablokován");
          reloadUsers();
        }
      })
      .catch((error) => {
        console.log(error.response.status);
        alert.error("Uživatel nemohl být zablokován");
      })
      .finally(() => {
        reloadUsers();
        hideBanModal();
      });
  };

  const handleUnbanUser = () => {
    axios
      .put(host + unbanUser + selectedRow?.userId, {})
      .then((response) => {
        if (response.status === 200) {
          alert.success("Uživatel byl odblokován");
        }
      })
      .catch((error) => {
        console.log(error.response.status);
        alert.error("Uživatel nemohl být odblokován");
      })
      .finally(() => {
        reloadUsers();
        hideUnbanModal();
      });
  };

  const handleDeleteUser = () => {
    axios
      .delete(host + deleteUser + selectedRow?.userId, {})
      .then((response) => {
        if (response.status === 200) {
          alert.info("Uživatel byl smazán");
        }
      })
      .catch((error) => {
        console.log(error.response.status);
        alert.error("Uživatel nemohl být smazán");
      })
      .finally(() => {
        reloadUsers();
        hideDeleteModal();
      });
  };

  const hideBanModal = () => {
    setIsBanModalOpen(false);
  };

  const hideUnbanModal = () => {
    setIsUnbanModalOpen(false);
  };

  const hidePromoteModal = () => {
    setIsPromoteModalOpen(false);
  };

  const hideDeleteModal = () => {
    setIsDeleteModalOpen(false);
  };

  const showBanModal = () => {
    setIsBanModalOpen(true);
  };

  const showUnbanModal = () => {
    setIsUnbanModalOpen(true);
  };

  const showPromoteModal = () => {
    setIsPromoteModalOpen(true);
  };

  const showDeleteModal = () => {
    setIsDeleteModalOpen(true);
  };

  const columns = [
    {
      dataField: "userId",
      text: "ID uživatele",
      filter: textFilter({ placeholder: "1" }),
      editable: false,
    },
    {
      dataField: "fullName",
      text: "Celé jméno",
      filterValue: (cell, row) => `${row.firstName} ${row.lastName}`,
      filter: textFilter({ placeholder: "Čestmír Strakatý" }),
      formatter: (cell, row) => {
        return `${row.firstName} ${row.lastName}`;
      },
      editable: false,
    },

    {
      dataField: "email",
      filter: textFilter({ placeholder: "cesmitr.strakaty@gmail.com" }),
      text: "Email",
      editable: false,
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
      editable: false,
    },
    {
      dataField: "locked",
      text: "Zablokovaný",
      formatter: booleanFormater,
      filter: selectFilter({
        defaultValue: "Ne",
        options: {
          true: "Ano",
          false: "Ne",
        },
        placeholder: "Vyberte stav",
        defaultValue: false,
      }),

      editor: {
        type: "select",
        options: booleanOptions,
      },
    },
    {
      dataField: "role",
      text: "Role uživatele",
      formatter: roleFormatter,
      filter: selectFilter({
        options: {
          ROLE_ADMIN: "Admin",
          ROLE_USER: "Uživatel",
          ROLE_SUPER_ADMIN: "Superadmin",
        },
        placeholder: "Uživatel",
        defaultValue: false,
      }),
      editable: ({ row }) => row?.role == "ROLE_USER",
      editor: {
        type: "select",
        placeholder: "role",
        options: roleOptions,
        defaultValue: "Uživatel",
      },
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
          onClick={showBanModal}
          variant="secondary"
          disabled={
            selectedRow == null ||
            selectedRow?.role !== "ROLE_USER" ||
            selectedRow?.locked
          }
        >
          Zablokovat
        </Button>
        <Button
          onClick={showUnbanModal}
          variant="success"
          disabled={
            selectedRow == null ||
            selectedRow?.role !== "ROLE_USER" ||
            !selectedRow.locked
          }
        >
          Odblokovat
        </Button>
        <Button
          onClick={showDeleteModal}
          variant="danger"
          disabled={
            selectedRow == null ||
            selectedRow?.role === "ROLE_SUPER_ADMIN" ||
            selectedRow?.role === "ROLE_ADMIN"
          }
        >
          Smazat
        </Button>
        {globalState?.user?.role === "ROLE_SUPER_ADMIN" && (
          <Button
            onClick={showPromoteModal}
            variant="primary"
            disabled={selectedRow == null || selectedRow?.role !== "ROLE_USER"}
          >
            Povýšit
          </Button>
        )}
      </ButtonGroup>
      <BootstrapTable
        keyField="userId"
        columns={columns}
        data={users}
        striped
        pagination={paginationFactory()}
        filter={filterFactory()}
        selectRow={selectRow}
      />

      <Modal
        isOpen={isBanModalOpen}
        on
        backdrop="static"
        size="sm"
        centered={true}
      >
        <ModalHeader>Zablokovat uživatele</ModalHeader>
        <ModalBody>
          {"Opravdu si přejete uživatele " +
            selectedRow?.email +
            " zablokovat?"}
        </ModalBody>
        <ModalFooter>
          <Button variant="secondary" onClick={hideBanModal}>
            Ne
          </Button>
          <Button variant="primary" onClick={handleBanUser}>
            Ano
          </Button>
        </ModalFooter>
      </Modal>
      <Modal
        isOpen={isUnbanModalOpen}
        on
        backdrop="static"
        size="sm"
        centered={true}
      >
        <ModalHeader>Odblokovat uživatele</ModalHeader>
        <ModalBody>
          {"Opravdu si přejete uživatele " +
            selectedRow?.email +
            " odblokovat?"}
        </ModalBody>
        <ModalFooter>
          <Button variant="secondary" onClick={hideUnbanModal}>
            Ne
          </Button>
          <Button variant="primary" onClick={handleUnbanUser}>
            Ano
          </Button>
        </ModalFooter>
      </Modal>
      <Modal
        isOpen={isPromoteModalOpen}
        on
        backdrop="static"
        size="sm"
        centered={true}
      >
        <ModalHeader>Změna role</ModalHeader>
        <ModalBody>
          {"Opravdu si přejete uživateli " +
            selectedRow?.email +
            " přiřadit roli Administrátor?"}
        </ModalBody>
        <ModalFooter>
          <Button variant="secondary" onClick={hidePromoteModal}>
            Ne
          </Button>
          <Button variant="primary" onClick={handlePromoteUser}>
            Ano
          </Button>
        </ModalFooter>
      </Modal>
      <Modal
        isOpen={isDeleteModalOpen}
        on
        backdrop="static"
        size="sm"
        centered={true}
      >
        <ModalHeader>Smazat uživatele</ModalHeader>
        <ModalBody>
          {"Opravdu si přejte trvale smazat uživatele " +
            selectedRow?.email +
            "?"}
        </ModalBody>
        <ModalFooter>
          <Button variant="secondary" onClick={hideDeleteModal}>
            Ne
          </Button>
          <Button variant="primary" onClick={handleDeleteUser}>
            Ano
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
};

export default UserTable;
