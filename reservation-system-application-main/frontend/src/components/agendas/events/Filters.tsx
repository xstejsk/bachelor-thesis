import React from "react";
import { Box } from "@mui/joy";
import { useForm, Controller } from "react-hook-form";
import { useQuery } from "@tanstack/react-query";
import { FormControl } from "@mui/joy";
import { FormLabel } from "@mui/joy";
import { Input } from "@mui/joy";
import { Select } from "@mui/joy";
import { Option } from "@mui/joy";
import { Tooltip } from "@mui/joy";
import { IconButton } from "@mui/joy";
import SyncIcon from "@mui/icons-material/Sync";
import SearchIcon from "@mui/icons-material/Search";
import calendarsApi from "../../../service/calendarsApi";
import { Calendar } from "../../../models/calendar";
import EventsTable from "./EventsTable";
import { Modal } from "@mui/joy";
import { ModalDialog } from "@mui/joy";
import { ModalClose } from "@mui/joy";
import { Typography } from "@mui/joy";
import { Divider } from "@mui/joy";
import { Sheet } from "@mui/joy";
import FilterAltIcon from "@mui/icons-material/FilterAlt";
import { useSearchParams } from "react-router-dom";
import { useResetSearchParams } from "../../../hooks/useResetSearchParams";

export type FilterParams = {
  from?: string;
  calendarId?: string;
};

const Filters = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [open, setOpen] = React.useState(false);
  const { reset: resetSearchParams } = useResetSearchParams([]);

  const handleSubmitFilter = (data: FilterParams) => {
    const params = new URLSearchParams(searchParams);
    if (data.from) params.set("from", data.from);
    if (data.calendarId) params.set("calendarId", data.calendarId);
    params.set("page", "0");
    setSearchParams(params);
    setOpen(false);
  };

  const {
    data: calendars,
    isLoading: isLoadingCalendars,
    isError: isErrorCalendars,
  } = useQuery({
    queryKey: ["calendars"],
    queryFn: () => calendarsApi.getAll(new URLSearchParams()),
  });

  const { register, handleSubmit, control, reset } = useForm<FilterParams>();

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
              <FormLabel>Kalendář</FormLabel>

              <Controller
                name="calendarId"
                control={control}
                render={({ field: { onChange } }) => (
                  <Select
                    // value={value}
                    size="md"
                    placeholder="Kalendář událostí"
                    sx={{
                      minWidth: 200,
                    }}
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
            <FormControl size="md">
              <FormLabel>Ode dne</FormLabel>
              <Input size="md" type="date" {...register("from")} />
            </FormControl>
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
                  resetSearchParams();
                  reset();
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

      <EventsTable />
    </React.Fragment>
  );
};

export default Filters;
