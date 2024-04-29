import * as React from "react";
import Avatar from "@mui/joy/Avatar";
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
import reservationsApi from "../../../service/reservationsApi";
import toast from "react-hot-toast";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import ConfirmationModal from "../../ConfirmationModal";
import { Reservation } from "../../../models/calendar";
import { Order } from "../utils";
import { AxiosError } from "axios";
import CustomGridLoader from "../../GridLoader";
import { parseISO } from "date-fns";
import Pagination from "../Pagination";
import { useSearchParams } from "react-router-dom";
import authAtom from "../../../state/authAtom";
import { useRecoilValue } from "recoil";
import { useLocation } from "react-router-dom";
import CheckRoundedIcon from "@mui/icons-material/CheckRounded";
import BlockIcon from "@mui/icons-material/Block";
import useUsersBalance from "../../../hooks/useUsersBalance";

export default function ReservationsTable() {
  const [order, setOrder] = React.useState<Order>("desc");
  const [searchParams, setSearchParams] = useSearchParams();
  const queryClient = useQueryClient();
  const auth = useRecoilValue(authAtom);
  const location = useLocation();
  const { updateCurrentBalance } = useUsersBalance();

  const [openConfirmationModal, setOpenConfirmationModal] = React.useState({
    open: false,
    id: "",
  });
  const isAgenda = !(location.pathname === "/reservations");
  React.useEffect(() => {
    const params = new URLSearchParams(searchParams);
    if (!isAgenda) {
      params.set("ownerId", auth.user.id);
    }
    if (params.get("page") === null || Number(params.get("page")) < 0)
      params.set("page", "0");
    if (params.get("size") === null || Number(params.get("size")) <= 0)
      params.set("size", "10");

    setSearchParams(params);
  }, []);

  const { mutate: deleteReservation } = useMutation({
    mutationFn: reservationsApi.deleteReservation,
    onSuccess: (response) => {
      queryClient.invalidateQueries(["reservations", searchParams.toString()]);
      updateCurrentBalance(response.owner.balance);
      toast.success("Rezervace byla zrušena");
    },
    onError: (error: AxiosError) => {
      if (error?.response?.status === 400) {
        toast.error("Rezervaci již nelze zrušit");
      }
    },
  });

  const {
    data: rows,
    isLoading: isLoadingReservations,
    isError: isErrorReservations,
  } = useQuery({
    queryKey: ["reservations", searchParams.toString()],
    queryFn: () => reservationsApi.getAll(searchParams),
  });

  if (isLoadingReservations) {
    return <CustomGridLoader />;
  }

  if (isErrorReservations) {
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
        {isLoadingReservations ? (
          <CustomGridLoader />
        ) : isErrorReservations ? (
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
              <Typography level="title-md">
                Seznam rezervací uživatelů na události
              </Typography>
            </caption>
            <thead>
              <tr>
                <th style={{ width: 160, padding: "12px 1rem" }}>
                  <Typography level="title-md">Událost</Typography>
                </th>

                <th style={{ width: 230, padding: "12px 6px" }}>
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
                  <Typography level="title-md">Uplatněna Multisport</Typography>
                </th>
                <th style={{ width: 140, padding: "12px 6px" }}>
                  <Typography level="title-md">Cena</Typography>
                </th>
                {isAgenda && (
                  <th style={{ width: 240, padding: "12px 6px" }}>
                    <Typography level="title-md">Uživatel</Typography>
                  </th>
                )}
                <th style={{ width: 210, padding: "12px 6px" }}> </th>
              </tr>
            </thead>
            <tbody>
              {rows.content.map((row: Reservation) => (
                <tr key={row.id}>
                  <td>
                    <Typography sx={{ paddingLeft: "1rem" }} level="body-md">
                      {row.event.title}
                    </Typography>
                  </td>

                  <td>
                    <Typography level="body-md">
                      {`${new Date(
                        row.event.date
                      ).toLocaleDateString()}, ${row.event.startTime.substring(
                        0,
                        5
                      )} - ${row.event.endTime.substring(0, 5)}`}
                    </Typography>
                  </td>
                  <td>
                    <Chip
                      variant="soft"
                      size="md"
                      startDecorator={
                        row.discountApplied ? (
                          <CheckRoundedIcon />
                        ) : (
                          <BlockIcon />
                        )
                      }
                      color={row.discountApplied ? "primary" : "danger"}
                    >
                      {row.discountApplied ? "Ano" : "Ne"}
                    </Chip>
                  </td>
                  <td>
                    <Chip variant="soft" size="md">
                      <Typography level="body-md" endDecorator="Kr.">
                        {row.discountApplied
                          ? row.event.discountPrice
                          : row.event.price}
                      </Typography>
                    </Chip>
                  </td>
                  {isAgenda && (
                    <td>
                      <Box
                        sx={{ display: "flex", gap: 2, alignItems: "center" }}
                      >
                        <Avatar size="sm">{row.owner.firstName.at(0)}</Avatar>
                        <div>
                          <Typography level="body-md">
                            {row.owner.firstName} {row.owner.lastName}
                          </Typography>
                          <Typography level="body-md">
                            {row.owner.email}
                          </Typography>
                        </div>
                      </Box>
                    </td>
                  )}
                  <td>
                    <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
                      <Tooltip title="Zrušit rezervaci">
                        <IconButton
                          size="md"
                          variant="plain"
                          color="danger"
                          onClick={() =>
                            setOpenConfirmationModal({ open: true, id: row.id })
                          }
                          disabled={
                            parseISO(
                              row.event.date + "T" + row.event.startTime
                            ) < new Date()
                          }
                        >
                          <DeleteIcon />
                        </IconButton>
                      </Tooltip>
                    </Box>
                  </td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <td colSpan={isAgenda ? 6 : 5} style={{ textAlign: "center" }}>
                  <Typography level="body-md">
                    {rows.totalElements} rezervací
                  </Typography>
                </td>
              </tr>
            </tfoot>
          </Table>
        )}
      </Sheet>
      <Pagination
        totalPages={rows.totalPages}
        isLoading={isLoadingReservations}
        isError={isErrorReservations}
      />
      <ConfirmationModal
        open={openConfirmationModal.open}
        setOpen={(open) =>
          setOpenConfirmationModal({ open: open, id: openConfirmationModal.id })
        }
        title="Zrušit rezervaci"
        challenge="Opravdu chcete zrušit tuto rezervaci?"
        onConfirm={() => deleteReservation(openConfirmationModal.id)}
      />
    </React.Fragment>
  );
}
