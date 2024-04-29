import * as React from "react";
import Avatar from "@mui/joy/Avatar";
import Box from "@mui/joy/Box";
import Chip from "@mui/joy/Chip";
import { Link, Tooltip } from "@mui/joy";
import CheckRoundedIcon from "@mui/icons-material/CheckRounded";
import BlockIcon from "@mui/icons-material/Block";
import Table from "@mui/joy/Table";
import Sheet from "@mui/joy/Sheet";
import IconButton from "@mui/joy/IconButton";
import Typography from "@mui/joy/Typography";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import { useQuery } from "@tanstack/react-query";
import toast from "react-hot-toast";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import AddIcon from "@mui/icons-material/Add";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";
import ConfirmationModal from "../../ConfirmationModal";
import { Order, stableSort, getComparator } from "../utils";
import usersApi from "../../../service/usersApi";
import CustomGridLoader from "../../GridLoader";
import { Role, User, defaultUser } from "../../../models/user";
import * as FaIcons from "react-icons/fa";
import { Link as RouterLink } from "react-router-dom";
import authAtom from "../../../state/authAtom";
import { useRecoilValue } from "recoil";
import { AxiosError } from "axios";
import FormModal from "../../FormModal";
import AddCreditsForm from "../../forms/AddCreditsForm";
import { useSearchParams } from "react-router-dom";
import Pagination from "../Pagination";
import useUsersBalance from "../../../hooks/useUsersBalance";

type OpenBalanceModal = {
  open: boolean;
  user: User;
};

export default function UsersTable() {
  const [order, setOrder] = React.useState<Order>("desc");
  const queryClient = useQueryClient();
  const [searchParams, setSearchParams] = useSearchParams();
  const auth = useRecoilValue(authAtom);
  const { updateCurrentBalance } = useUsersBalance();
  const [openBalanceModal, setOpenBalanceModal] =
    React.useState<OpenBalanceModal>({
      open: false,
      user: defaultUser,
    });
  const [openConfirmationModal, setOpenConfirmationModal] = React.useState({
    open: false,
    id: "",
    role: Role.USER,
  });

  React.useEffect(() => {
    const params = new URLSearchParams(searchParams);
    if (params.get("page") === null || Number(params.get("page")) < 0)
      params.set("page", "0");
    if (params.get("size") === null || Number(params.get("size")) <= 0)
      params.set("size", "10");
    setSearchParams(params);
  }, []);

  const { mutate: updateRole } = useMutation({
    mutationFn: usersApi.updateRole,
    onSuccess: () => {
      queryClient.invalidateQueries(["users", searchParams.toString()]);
      toast.success("Role uživatele byla změněna.");
    },
    onError: (error: AxiosError) => {
      if (error?.response?.status === 400) {
        toast.error("Roli uživatele nelze změnit.");
      }
    },
  });

  const { mutate: updateBalance } = useMutation({
    mutationFn: usersApi.updateBalance,
    onSuccess: (response) => {
      queryClient.invalidateQueries(["users", searchParams.toString()]);
      if (response.id === auth.user?.id) {
        import.meta.env.DEBUG && console.log("updating balance");
        updateCurrentBalance(response.balance);
      }
      toast.success("Kredity přidány.");
    },
    onError: () => {
      toast.error("Něco se pokazilo.");
    },
  });

  const {
    data: rows,
    isLoading: isLoadingUsers,
    isError: isErrorUsers,
  } = useQuery({
    queryKey: ["users", searchParams.toString()],
    queryFn: () => usersApi.getAll(searchParams),
  });

  if (isLoadingUsers) {
    return <CustomGridLoader />;
  }

  if (isErrorUsers) {
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
        {isLoadingUsers ? (
          <CustomGridLoader />
        ) : isErrorUsers ? (
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
              <Typography level="title-md">Seznam uživatelů</Typography>
            </caption>
            <thead>
              <tr>
                <th style={{ width: 250, padding: "12px 1rem" }}>
                  <Typography level="title-md">Email</Typography>
                </th>
                <th style={{ width: 180, padding: "12px 6px" }}>
                  <Link
                    underline="none"
                    color="primary"
                    component="button"
                    onClick={() => setOrder(order === "asc" ? "desc" : "asc")}
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
                    <Typography level="title-md">Jméno a příjmení</Typography>
                  </Link>
                </th>
                <th style={{ width: 160, padding: "12px 6px" }}>
                  <Typography level="title-md">Držitel Multisport</Typography>
                </th>
                <th style={{ width: 160, padding: "12px 6px" }}>
                  <Typography level="title-md">Kredity</Typography>
                </th>
                <th style={{ width: 160, padding: "12px 6px" }}>
                  <Typography level="title-md">Status</Typography>
                </th>
                <th style={{ width: 160, padding: "12px 6px" }}>
                  <Typography level="title-md">Role</Typography>
                </th>
                <th style={{ width: 100, padding: "12px 6px" }}></th>
              </tr>
            </thead>
            <tbody>
              {stableSort(rows.content, getComparator(order, "id")).map(
                (row: User) => (
                  <tr key={row.id}>
                    <td>
                      <Box
                        sx={{ display: "flex", gap: 2, alignItems: "center" }}
                      >
                        <Avatar size="sm">{row.firstName.at(0)}</Avatar>
                        <div>
                          <Typography level="body-md">{row.email}</Typography>
                        </div>
                      </Box>
                    </td>
                    <td>
                      <Typography level="body-md">
                        {row.firstName} {row.lastName}
                      </Typography>
                    </td>
                    <td>
                      <Chip
                        variant="soft"
                        size="sm"
                        startDecorator={
                          row.hasDailyDiscount ? (
                            <CheckRoundedIcon />
                          ) : (
                            <BlockIcon />
                          )
                        }
                        color={row.hasDailyDiscount ? "primary" : "danger"}
                      >
                        <Typography level="body-md">
                          {row.hasDailyDiscount ? "Ano" : "Ne"}
                        </Typography>
                      </Chip>
                    </td>
                    <td>
                      <Box
                        sx={{
                          display: "flex",
                          gap: 1,
                          alignItems: "center",
                          justifyContent: "space-between",
                          width: 130,
                        }}
                      >
                        <Chip variant="soft" size="md">
                          <Typography level="body-md" endDecorator="Kr.">
                            {row.balance}
                          </Typography>
                        </Chip>
                        <Tooltip title="Přidat kredity">
                          <IconButton
                            size="sm"
                            variant="plain"
                            color="primary"
                            sx={{ borderRadius: "50%" }}
                            onClick={() =>
                              setOpenBalanceModal({ open: true, user: row })
                            }
                          >
                            <AddIcon />
                          </IconButton>
                        </Tooltip>
                      </Box>
                    </td>
                    <td>
                      <Chip
                        variant="soft"
                        size="sm"
                        startDecorator={
                          row.enabled ? <CheckRoundedIcon /> : <BlockIcon />
                        }
                        color={row.enabled ? "primary" : "danger"}
                      >
                        <Typography level="body-md">
                          {row.enabled ? "Potvrzen" : "Čeká na potvrzení"}
                        </Typography>
                      </Chip>
                    </td>
                    <td>
                      <Box
                        sx={{
                          display: "flex",
                          gap: 1,
                          alignItems: "center",
                          width: 130,
                          justifyContent: "space-between",
                        }}
                      >
                        {row.role === Role.ADMIN ? (
                          <Typography
                            startDecorator={<FaIcons.FaUserGraduate />}
                            level="body-md"
                          >
                            Admin
                          </Typography>
                        ) : (
                          <Typography
                            startDecorator={<FaIcons.FaUser />}
                            level="body-md"
                          >
                            Uživatel
                          </Typography>
                        )}
                        <Tooltip
                          title={
                            row.role === Role.ADMIN
                              ? "Změnit na uživatele"
                              : "Povýšit na administrátora"
                          }
                        >
                          <IconButton
                            size="sm"
                            variant="plain"
                            color="primary"
                            sx={{ borderRadius: "50%" }}
                            disabled={row.id === auth.user?.id}
                            onClick={() =>
                              setOpenConfirmationModal({
                                open: true,
                                id: row.id,
                                role:
                                  row.role === Role.ADMIN
                                    ? Role.USER
                                    : Role.ADMIN,
                              })
                            }
                          >
                            {row.role === Role.ADMIN ? (
                              <ArrowDownwardIcon />
                            ) : (
                              <ArrowUpwardIcon />
                            )}
                          </IconButton>
                        </Tooltip>
                      </Box>
                    </td>
                    <td>
                      <Typography level="body-md">
                        <RouterLink
                          to={`/agendas/reservations?ownerId=${row.id}`}
                          rel="noreferrer"
                          target={"_blank"}
                          style={{ textDecoration: "none" }}
                        >
                          Rezervace
                        </RouterLink>
                      </Typography>
                    </td>
                  </tr>
                )
              )}
            </tbody>
            <tfoot>
              <tr>
                <td colSpan={7} style={{ textAlign: "center" }}>
                  <Typography level="body-md">
                    {rows.totalElements} uživatelů
                  </Typography>
                </td>
              </tr>
            </tfoot>
          </Table>
        )}
      </Sheet>
      <Pagination
        isLoading={isLoadingUsers}
        totalPages={rows.totalPages}
        isError={isErrorUsers}
      />
      <ConfirmationModal
        open={openConfirmationModal.open}
        setOpen={(open) =>
          setOpenConfirmationModal({ ...openConfirmationModal, open })
        }
        title="Změna role"
        challenge={
          openConfirmationModal.role === Role.ADMIN
            ? "Opravdu chcete povýšit uživatele na administrátora?"
            : "Opravdu chcete snížit administrátora na uživatele?"
        }
        onConfirm={() => updateRole({ ...openConfirmationModal })}
      />

      <FormModal
        open={openBalanceModal.open}
        setOpen={(open) => setOpenBalanceModal({ ...openBalanceModal, open })}
        title={"Připsat kredity"}
        challenge={`Připsat kredity uživateli ${openBalanceModal.user?.firstName} ${openBalanceModal.user?.lastName}`}
        description={undefined}
        form={
          <AddCreditsForm
            updateBalance={updateBalance}
            closeForm={() =>
              setOpenBalanceModal({
                open: false,
                user: defaultUser,
              })
            }
            user={openBalanceModal.user!}
          />
        }
      />
    </React.Fragment>
  );
}
