import React from "react";
import { Box } from "@mui/joy";
import { useForm, Controller } from "react-hook-form";
import { FormControl } from "@mui/joy";
import { FormLabel } from "@mui/joy";
import { Input } from "@mui/joy";
import { Select } from "@mui/joy";
import { Option } from "@mui/joy";
import { Tooltip } from "@mui/joy";
import { IconButton } from "@mui/joy";
import SyncIcon from "@mui/icons-material/Sync";
import SearchIcon from "@mui/icons-material/Search";
import { Role } from "../../../models/user";
import UsersTable from "./UsersTable";
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
  fulltext?: string;
  role?: Role;
};

const Filters = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const { reset: resetSearchParams } = useResetSearchParams([]);
  const [open, setOpen] = React.useState(false);

  const handleSubmitFilter = (data: FilterParams) => {
    const params = new URLSearchParams(searchParams);
    if (data.fulltext) params.set("fulltext", data.fulltext);
    if (data.role) params.set("role", data.role);
    params.set("page", "0");
    setSearchParams(params);
    setOpen(false);
  };

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
              <FormLabel>Fulltext</FormLabel>
              <Input
                size="md"
                type="text"
                {...register("fulltext")}
                placeholder="Hledat všude"
                startDecorator={<SearchIcon />}
              />
            </FormControl>
            <FormControl size="md">
              <FormLabel>Role</FormLabel>

              <Controller
                name="role"
                control={control}
                render={({ field: { onChange, value } }) => (
                  <Select
                    value={value}
                    size="md"
                    placeholder="Uživatel"
                    slotProps={{ button: { sx: { whiteSpace: "nowrap" } } }}
                    onChange={(_event, value) => onChange(value)}
                  >
                    <Option value={Role.ADMIN}>Administrátor</Option>

                    <Option value={Role.USER}>Uživatel</Option>
                  </Select>
                )}
              />
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

      <UsersTable />
    </React.Fragment>
  );
};

export default Filters;
