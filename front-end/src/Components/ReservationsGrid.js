import React from "react";
import {
  ColumnDirective,
  ColumnsDirective,
  Filter,
  GridComponent,
  Edit,
  Toolbar,
  ToolbarItem,
} from "@syncfusion/ej2-react-grids";
import { useEffect, useState } from "react";
import axios from "axios";
import { mockReservationsEndpoint, host } from "../util/EndpointConfig";
import { Inject, Page, Sort } from "@syncfusion/ej2-react-grids";
import CustomGridLoader from "./CustomLoader";

const ReservationsGrid = () => {
  const pageSettings = { pageSize: 10 };
  const sortSettings = {
    columns: [{ field: "EmployeeID", direction: "Ascending" }],
  };
  const [reservations, setReservations] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

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

  const editOptions = {
    allowEditing: true,
    allowAdding: true,
    allowDeleting: true,
  };
  const toolbarOptions = ["Add", "Edit", "Delete", "Update", "Cancel"];
  const filterSettings = { ignoreAccent: true, type: "Menu" };
  if (isLoading) {
    return <CustomGridLoader isLoading={isLoading} />;
  } else {
    return (
      <div
        style={{
          justifyContent: "center",
          alignItems: "center",
          display: "flex",
          padding: 3,
          background: "#c93060",
        }}
      >
        <GridComponent
          dataSource={reservations}
          allowPaging={true}
          pageSettings={pageSettings}
          allowSorting={true}
          sortSettings={sortSettings}
          allowFiltering={true}
          filterSettings={filterSettings}
          editSettings={editOptions}
          toolbar={toolbarOptions}
        >
          <ColumnsDirective>
            <ColumnDirective
              headerText="ID uživatele"
              field="userId"
              width="100"
              textAlign="Right"
              type="number"
            />
            <ColumnDirective
              headerText="Jméno uživatele"
              field="userFullName"
              width="100"
              type="string"
            />
            <ColumnDirective
              headerText="ID události"
              field="eventId"
              width="100"
              textAlign="Right"
              type="number"
            />
            <ColumnDirective
              headerText="Subjekt"
              field="subject"
              width="100"
              textAlign="Left"
              type="string"
            />
            <ColumnDirective
              headerText="Začátek"
              field="startTime"
              width="100"
              type="datetime"
            />
            <ColumnDirective
              headerText="Konec"
              field="endTime"
              width="100"
              type="datetime"
            />
            <ColumnDirective
              headerText="Zrušeno"
              field="canceled"
              width="100"
              type="boolean"
            />
          </ColumnsDirective>
          <Inject services={[Page, Sort, Filter, Toolbar]} />
        </GridComponent>
      </div>
    );
  }
};

export default ReservationsGrid;
