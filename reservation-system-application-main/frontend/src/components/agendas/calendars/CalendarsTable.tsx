import * as React from "react";
import Box from "@mui/joy/Box";
import { Tooltip } from "@mui/joy";
import Table from "@mui/joy/Table";
import Sheet from "@mui/joy/Sheet";
import DeleteIcon from "@mui/icons-material/Delete";
import IconButton from "@mui/joy/IconButton";
import Typography from "@mui/joy/Typography";
import { useQuery } from "@tanstack/react-query";
import toast from "react-hot-toast";
import { Link } from "@mui/joy";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import ConfirmationModal from "../../ConfirmationModal";
import { Order } from "../utils";
import { AxiosError } from "axios";
import CustomGridLoader from "../../GridLoader";
import { Link as RouterLink, useSearchParams } from "react-router-dom";
import Pagination from "../Pagination";
import calendarsApi from "../../../service/calendarsApi";
import { Calendar, UpdateCalendarRequest } from "../../../models/calendar";
import EditIcon from "@mui/icons-material/Edit";
import FormModal from "../../FormModal";
import EditCalendarForm from "../../forms/EditCalendarForm";
export default function CalendarsTable() {
  const [order, setOrder] = React.useState<Order>("desc");
  const [searchParams, setSearchParams] = useSearchParams();
  const queryClient = useQueryClient();
  const [openConfirmationModal, setOpenConfirmationModal] = React.useState({
    open: false,
    id: "",
  });
  const [openEditModal, setOpenEditModal] = React.useState<{
    open: boolean;
    calendar: UpdateCalendarRequest;
  }>({
    open: false,
    calendar: {
      id: "",
      name: "",
      locationId: "",
      thumbnail: "",
    },
  });

  React.useEffect(() => {
    const params = new URLSearchParams(searchParams);
    if (params.get("page") === null || Number(params.get("page")) < 0)
      params.set("page", "0");
    if (params.get("size") === null || Number(params.get("size")) <= 0)
      params.set("size", "10");
    setSearchParams(params);
  }, []);

  const { mutate: deleteCalendar } = useMutation({
    mutationFn: calendarsApi.deleteCalendar,
    onSuccess: () => {
      queryClient.invalidateQueries(["calendars", searchParams.toString()]);
      toast.success("Kalendář byl smazán");
    },
    onError: (error: AxiosError) => {
      if (error?.response?.status === 400) {
        toast.error("Kalendář nelze smazat, protože obsahuje budoucí události");
      } else {
        toast.error("Nastala chyba");
      }
    },
  });

  const {
    data: rows,
    isLoading: isLoadingCalendars,
    isError: isErrorCalendars,
  } = useQuery({
    queryKey: ["calendars", searchParams.toString()],
    queryFn: () => calendarsApi.getAll(searchParams),
  });

  if (isLoadingCalendars) {
    return <CustomGridLoader />;
  }

  if (isErrorCalendars) {
    return <div>Error</div>;
  }

  return (
    <React.Fragment>
      <Sheet
        className="OrderTableContainer"
        variant="outlined"
        sx={{
          display: "flex",
          width: "100%",
          borderRadius: "sm",
          flexShrink: 1,
          overflow: "auto",
          //   overflowX: "scroll",
          minHeight: 0,
        }}
      >
        {isLoadingCalendars ? (
          <CustomGridLoader />
        ) : isErrorCalendars ? (
          <div>Error</div>
        ) : (
          <Table
            aria-labelledby="tableTitle"
            stickyHeader
            hoverRow
            sx={{
              "--TableCell-headBackground":
                "var(--joy-palette-background-level1)",
              "--Table-headerUnderlineThickness": "1px",
              "--TableRow-hoverBackground":
                "var(--joy-palette-background-level1)",
              "--TableCell-paddingY": "4px",
              "--TableCell-paddingX": "8px",
            }}
          >
            <caption>
              <Typography level="title-md">Seznam kalendářů</Typography>
            </caption>
            <thead>
              <tr>
                <th style={{ width: 150, padding: "12px 1rem" }}>
                  <Link
                    underline="none"
                    color="primary"
                    component="button"
                    onClick={() => {
                      rows.content.reverse() &&
                        setOrder(order === "desc" ? "asc" : "desc");
                    }}
                    fontWeight="lg"
                    endDecorator={<ArrowDropDownIcon />}
                    sx={{
                      "& svg": {
                        transition: "0.2s",
                        transform:
                          order === "desc" ? "rotate(0deg)" : "rotate(180deg)",
                      },
                    }}
                  >
                    <Typography level="title-md">Název</Typography>
                  </Link>
                </th>
                <th style={{ width: 150, padding: "12px 6px" }}>
                  <Typography level="title-md">Lokace</Typography>
                </th>
                <th style={{ width: 60, padding: "12px 6px" }}></th>
                <th style={{ width: 60, padding: "12px 6px" }}></th>
                <th style={{ width: 60, padding: "12px 6px" }}> </th>
              </tr>
            </thead>
            <tbody>
              {rows.content.map((row: Calendar) => (
                <tr key={row.id}>
                  <td>
                    <Typography sx={{ paddingLeft: "0.5rem" }} level="body-md">
                      {row.name}
                    </Typography>
                  </td>
                  <td>
                    <Typography level="body-md">{row.location.name}</Typography>
                  </td>
                  <td>
                    <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
                      <Tooltip title="Smazat kalendář">
                        <IconButton
                          size="sm"
                          variant="plain"
                          color="primary"
                          onClick={() =>
                            setOpenEditModal({
                              open: true,
                              calendar: {
                                id: row.id,
                                name: row.name,
                                locationId: row.location.id,
                                thumbnail: row.thumbnail,
                              },
                            })
                          }
                        >
                          <EditIcon />
                        </IconButton>
                      </Tooltip>
                    </Box>
                  </td>
                  <td>
                    <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
                      <Tooltip title="Smazat kalendář">
                        <IconButton
                          size="sm"
                          variant="plain"
                          color="danger"
                          onClick={() =>
                            setOpenConfirmationModal({ open: true, id: row.id })
                          }
                        >
                          <DeleteIcon />
                        </IconButton>
                      </Tooltip>
                    </Box>
                  </td>
                  <td>
                    <Typography level="body-md">
                      <RouterLink
                        to={`/agendas/events?calendarId=${row.id}`}
                        rel="noreferrer"
                        target={"_blank"}
                        style={{ textDecoration: "none" }}
                      >
                        Události
                      </RouterLink>
                    </Typography>
                  </td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <td colSpan={5} style={{ textAlign: "center" }}>
                  <Typography level="body-md">
                    {rows.totalElements} kalendářů
                  </Typography>
                </td>
              </tr>
            </tfoot>
          </Table>
        )}
      </Sheet>
      <Pagination
        totalPages={rows.totalPages}
        isLoading={isLoadingCalendars}
        isError={isErrorCalendars}
      />
      <FormModal
        open={openEditModal.open}
        setOpen={(open) => {
          setOpenEditModal({ open: open, calendar: openEditModal.calendar });
        }}
        title="Upravit kalendář"
        description="Zde můžete upravit kalendář"
        challenge={undefined}
        form={
          <EditCalendarForm
            withSubmit={() =>
              setOpenEditModal({
                open: false,
                calendar: openEditModal.calendar,
              })
            }
            calendar={openEditModal.calendar}
          />
        }
      />

      <ConfirmationModal
        open={openConfirmationModal.open}
        setOpen={(open) =>
          setOpenConfirmationModal({ open: open, id: openConfirmationModal.id })
        }
        title="Smazat kalendář"
        challenge="Opravdu chcete smazat tento kalendář?"
        onConfirm={() => deleteCalendar(openConfirmationModal.id)}
      />
    </React.Fragment>
  );
}
