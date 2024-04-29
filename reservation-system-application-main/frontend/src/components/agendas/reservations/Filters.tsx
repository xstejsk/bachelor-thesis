import React from "react";
import { Box } from "@mui/joy";
import { useForm, Controller } from "react-hook-form";
import { useQuery } from "@tanstack/react-query";
import { FormControl } from "@mui/joy";
import { FormLabel } from "@mui/joy";
import { Input } from "@mui/joy";
import { Select } from "@mui/joy";
import { Option } from "@mui/joy";
import { Autocomplete } from "@mui/joy";
import { CircularProgress } from "@mui/joy";
import { Tooltip } from "@mui/joy";
import { IconButton } from "@mui/joy";
import SyncIcon from "@mui/icons-material/Sync";
import SearchIcon from "@mui/icons-material/Search";
import calendarsApi from "../../../service/calendarsApi";
import usersApi from "../../../service/usersApi";
import { Role, User } from "../../../models/user";
import { Calendar } from "../../../models/calendar";
import ReservationsTable from "./ReservationsTable";
import { Modal } from "@mui/joy";
import { ModalDialog } from "@mui/joy";
import { ModalClose } from "@mui/joy";
import { Typography } from "@mui/joy";
import { Divider } from "@mui/joy";
import { Sheet } from "@mui/joy";
import FilterAltIcon from "@mui/icons-material/FilterAlt";
import { useSearchParams } from "react-router-dom";
import { useResetSearchParams } from "../../../hooks/useResetSearchParams";
import authAtom from "../../../state/authAtom";
import { useRecoilValue } from "recoil";

export type FilterParams = {
  from?: string;
  calendarId?: string;
  ownerId?: string;
  eventId?: string;
};

const Filters = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [open, setOpen] = React.useState(false);
  const auth = useRecoilValue(authAtom);
  const { reset: resetSearchParams } = useResetSearchParams(
    auth.user.role === Role.USER ? ["ownerId"] : []
  );
  const isAgenda = !(location.pathname === "/reservations");

  const handleSubmitFilter = (data: FilterParams) => {
    const params = new URLSearchParams(searchParams);
    if (data.from) params.set("from", data.from);
    if (data.calendarId) params.set("calendarId", data.calendarId);
    if (data.ownerId) params.set("ownerId", data.ownerId);
    params.set("page", "0");
    setSearchParams(params);
    setOpen(false);
  };

  const { register, handleSubmit, control, reset } = useForm<FilterParams>({
    defaultValues: {
      from: "",
      calendarId: "",
      ownerId: "",
    },
  });

  const {
    data: calendars,
    isLoading: isLoadingCalendars,
    isError: isErrorCalendars,
  } = useQuery({
    queryKey: ["calendars"],
    queryFn: () => calendarsApi.getAll(new URLSearchParams()),
  });

  const { data: users, isLoading: isLoadingUsers } = useQuery({
    queryKey: ["users"],
    queryFn: () => usersApi.getAll(new URLSearchParams()),
    enabled: auth.user.role === Role.ADMIN && isAgenda,
  });

  const renderFilters = () => {
    return (
      <form onSubmit={handleSubmit(handleSubmitFilter)}>
        <Box
          sx={{
            display: "flex",
            flexDirection: {
              xs: "column",
              sm: "column",
              md: "row",
            },
            alignItems: { md: "end" },
            // justifyContent: "left",
            my: 1,
            gap: 1,
          }}
        >
          <React.Fragment>
            <FormControl size="md">
              <FormLabel>Ode dne</FormLabel>
              <Input size="md" type="date" {...register("from")} />
            </FormControl>
            <FormControl size="md">
              <FormLabel>Kalendář</FormLabel>

              <Controller
                name="calendarId"
                control={control}
                render={({ field: { onChange, value } }) => (
                  <Select
                    value={value}
                    size="md"
                    placeholder="Kalendář událostí"
                    slotProps={{ button: { sx: { whiteSpace: "nowrap" } } }}
                    onChange={(_event, value) => onChange(value)}
                  >
                    {isLoadingCalendars ? (
                      <Option value="all">Načítání...</Option>
                    ) : isErrorCalendars ? (
                      <Option value="all">Chyba</Option>
                    ) : (
                      calendars?.content.map((calendar: Calendar) => (
                        <Option
                          key={calendar.id}
                          value={calendar.id}
                          label={calendar.name}
                        >
                          {calendar.name}
                        </Option>
                      ))
                    )}
                  </Select>
                )}
              />
            </FormControl>
            {auth?.user.role === Role.ADMIN && isAgenda && (
              <FormControl size="md">
                <FormLabel>Uživatel</FormLabel>
                <Controller
                  name="ownerId"
                  control={control}
                  render={({ field: { onChange } }) => (
                    <Autocomplete
                      placeholder="johnDoe@gmail.com"
                      isOptionEqualToValue={(option: User, value) =>
                        option.id === value.id
                      }
                      onChange={(_event, value) => onChange(value?.id)}
                      getOptionLabel={(option) => option.email}
                      options={users?.content ?? []}
                      loading={isLoadingUsers}
                      endDecorator={
                        isLoadingUsers ? (
                          <CircularProgress
                            size="md"
                            sx={{ bgcolor: "background.surface" }}
                          />
                        ) : null
                      }
                    />
                  )}
                />
              </FormControl>
            )}
            <Tooltip title="Vyhledat">
              <IconButton
                type="submit"
                color="primary"
                variant="solid"
                size="md"
              >
                <SearchIcon />
              </IconButton>
            </Tooltip>
            <Tooltip title="Obnovit">
              <IconButton
                onClick={() => {
                  reset();
                  resetSearchParams();
                  setOpen(false);
                }}
                color="primary"
                variant="outlined"
                size="md"
              >
                <SyncIcon />
              </IconButton>
            </Tooltip>
          </React.Fragment>
        </Box>
      </form>
    );
  };

  return (
    <React.Fragment>
      <Box
        className="SearchAndFilters-mobile"
        sx={{
          display: {
            xs: "flex",
            sm: "flex",
            md: "none",
          },
          my: 1,
          gap: 1,
        }}
      >
        <IconButton
          size="md"
          variant="outlined"
          color="neutral"
          onClick={() => setOpen(true)}
        >
          <FilterAltIcon />
        </IconButton>
        <Modal open={open} onClose={() => setOpen(false)}>
          <ModalDialog aria-labelledby="filter-modal" layout="fullscreen">
            <ModalClose />
            <Typography id="filter-modal" level="h2">
              Filtry
            </Typography>
            <Divider sx={{ my: 2 }} />
            <Sheet sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
              {renderFilters()}
            </Sheet>
          </ModalDialog>
        </Modal>
      </Box>

      <Box
        display={{
          xs: "none",
          sm: "none",
          md: "flex",
        }}
      >
        {renderFilters()}
      </Box>

      <ReservationsTable />
    </React.Fragment>
  );
};

export default Filters;
