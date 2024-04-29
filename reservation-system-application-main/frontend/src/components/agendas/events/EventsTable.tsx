import * as React from "react";
import Box from "@mui/joy/Box";
import { Tooltip } from "@mui/joy";
import Chip from "@mui/joy/Chip";
import Link from "@mui/joy/Link";
import Table from "@mui/joy/Table";
import Sheet from "@mui/joy/Sheet";
import DeleteIcon from "@mui/icons-material/Delete";
import IconButton from "@mui/joy/IconButton";
import Typography from "@mui/joy/Typography";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import { useQuery } from "@tanstack/react-query";
import toast from "react-hot-toast";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import ConfirmationModal from "../../ConfirmationModal";
import { Order } from "../utils";
import { AxiosError } from "axios";
import CustomGridLoader from "../../GridLoader";
import { Link as RouterLink, useSearchParams } from "react-router-dom";
import { Event } from "../../../models/calendar";
import eventsApi from "../../../service/eventsApi";
import * as BsIcons from "react-icons/bs";
import { parseISO } from "date-fns";
import Pagination from "../Pagination";
import EditIcon from "@mui/icons-material/Edit";
import FormModal from "../../FormModal";
import EditEventForm from "../../forms/EditEventForm";

export default function EventsTable() {
  const [order, setOrder] = React.useState<Order>("desc");
  const [searchParams, setSearchParams] = useSearchParams();
  const queryClient = useQueryClient();
  const [openConfirmationModal, setOpenConfirmationModal] = React.useState({
    open: false,
    id: "",
  });
  const [openEditModal, setOpenEditModal] = React.useState<{
    open: boolean;
    event: Event | undefined;
  }>({
    open: false,
    event: undefined,
  });

  React.useEffect(() => {
    const params = new URLSearchParams(searchParams);
    if (params.get("page") === null || Number(params.get("page")) < 0)
      params.set("page", "0");
    if (params.get("size") === null || Number(params.get("size")) <= 0)
      params.set("size", "10");
    setSearchParams(params);
  }, []);

  const { mutate: deleteEvent } = useMutation({
    mutationFn: eventsApi.deleteEvent,
    onSuccess: () => {
      queryClient.invalidateQueries(["events", searchParams.toString()]);
      toast.success("Událost byla zrušena");
    },
    onError: (error: AxiosError) => {
      if (error?.response?.status === 400) {
        toast.error("Událost již nelze zrušit");
      } else {
        toast.error("Nastala chyba");
      }
    },
  });

  const { mutate: updateEvent } = useMutation({
    mutationFn: eventsApi.updateEvent,
    onSuccess: () => {
      queryClient.invalidateQueries(["events", searchParams.toString()]);
      toast.success("Detail události byl upraven");
    },
    onError: (error: AxiosError) => {
      if (error?.response?.status === 400) {
        toast.error("Událost již nelze upravit");
      } else {
        toast.error("Nastala chyba");
      }
    },
  });

  const {
    data: rows,
    isLoading: isLoadingEvents,
    isError: isErrorLoadingEvents,
  } = useQuery({
    queryKey: ["events", searchParams.toString()],
    queryFn: () => eventsApi.getAll(searchParams),
  });

  if (isLoadingEvents) {
    return <CustomGridLoader />;
  }

  if (isErrorLoadingEvents) {
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
        {isLoadingEvents ? (
          <CustomGridLoader />
        ) : isErrorLoadingEvents ? (
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
              <Typography level="title-md">Seznam událostí</Typography>
            </caption>
            <thead>
              <tr>
                <th style={{ width: 240, padding: "12px 1rem" }}>
                  <Typography level="title-md">Událost</Typography>
                </th>
                {/* <th style={{ width: 140, padding: "12px 1rem" }}>Kalendář</th> */}
                <th style={{ width: 240, padding: "12px 6px" }}>
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
                    <Typography level="title-md">Datum a čas</Typography>
                  </Link>
                </th>
                <th style={{ width: 140, padding: "12px 6px" }}>
                  <Typography level="title-md">Cena</Typography>
                </th>
                <th style={{ width: 160, padding: "12px 6px" }}>
                  <Typography level="title-md">Cena s Multisport</Typography>
                </th>
                <th style={{ width: 130, padding: "12px 6px" }}>
                  <Typography level="title-md">Obsazenost</Typography>
                </th>
                <th style={{ width: 110, padding: "12px 6px" }}> </th>
                <th style={{ width: 110, padding: "12px 6px" }}> </th>
                <th style={{ width: 100, padding: "12px 6px" }}></th>
              </tr>
            </thead>
            <tbody>
              {rows.content.map((row: Event) => (
                <tr key={row.id}>
                  <td>
                    <Typography sx={{ paddingLeft: "1rem" }} level="body-md">
                      {row.title}
                    </Typography>
                  </td>

                  <td>
                    <Typography level="body-md">
                      {`${new Date(
                        row.date
                      ).toLocaleDateString()}, ${row.startTime.substring(
                        0,
                        5
                      )} - ${row.endTime.substring(0, 5)}`}
                    </Typography>
                  </td>
                  <td>
                    <Chip variant="soft" size="md">
                      <Typography level="body-md" endDecorator="Kr.">
                        {row.price}
                      </Typography>
                    </Chip>
                  </td>
                  <td>
                    <Chip variant="soft" size="md">
                      <Typography level="body-md" endDecorator="Kr.">
                        {row.discountPrice}
                      </Typography>
                    </Chip>
                  </td>
                  <td>
                    <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
                      <Typography
                        level="body-md"
                        startDecorator={<BsIcons.BsPerson />}
                      >
                        {`${row.maximumCapacity - row.spacesAvailable}/${
                          row.maximumCapacity
                        }`}
                      </Typography>
                    </Box>
                  </td>
                  <td>
                    <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
                      <Tooltip title="Upravit událost">
                        <IconButton
                          size="md"
                          variant="plain"
                          color="primary"
                          disabled={
                            parseISO(row.date + "T" + row.startTime) <
                            new Date()
                          }
                          onClick={() =>
                            setOpenEditModal({ open: true, event: row })
                          }
                        >
                          <EditIcon />
                        </IconButton>
                      </Tooltip>
                    </Box>
                  </td>
                  <td>
                    <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
                      <Tooltip title="Zrušit událost">
                        <IconButton
                          size="md"
                          variant="plain"
                          color="danger"
                          disabled={
                            row.spacesAvailable !== row.maximumCapacity ||
                            parseISO(row.date + "T" + row.startTime) <
                              new Date()
                          }
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
                        to={`/agendas/reservations?eventId=${row.id}`}
                        rel="noreferrer"
                        target={"_blank"}
                        style={{ textDecoration: "none" }}
                      >
                        Rezervace
                      </RouterLink>
                    </Typography>
                  </td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <td colSpan={8} style={{ textAlign: "center" }}>
                  <Typography level="body-md">
                    {rows.totalElements} událostí
                  </Typography>
                </td>
              </tr>
            </tfoot>
          </Table>
        )}
      </Sheet>
      <Pagination
        totalPages={rows.totalPages}
        isLoading={isLoadingEvents}
        isError={isErrorLoadingEvents}
      />
      <ConfirmationModal
        open={openConfirmationModal.open}
        setOpen={(open) =>
          setOpenConfirmationModal({ open: open, id: openConfirmationModal.id })
        }
        title="Zrušit událost"
        challenge="Opravdu chcete zrušit tuto událost?"
        onConfirm={() => deleteEvent(openConfirmationModal.id)}
      />
      <FormModal
        challenge={undefined}
        open={openEditModal.open}
        setOpen={(open) => setOpenEditModal({ open: open, event: undefined })}
        title="Upravit událost"
        description="Zde můžete upravit událost"
        form={
          <EditEventForm
            event={openEditModal.event!}
            closeForm={() =>
              setOpenEditModal({ open: false, event: undefined })
            }
            updateEvent={updateEvent}
          />
        }
      />
    </React.Fragment>
  );
}
