import KeyboardArrowRightIcon from "@mui/icons-material/KeyboardArrowRight";
import KeyboardArrowLeftIcon from "@mui/icons-material/KeyboardArrowLeft";
import KeyboardDoubleArrowLeftIcon from "@mui/icons-material/KeyboardDoubleArrowLeft";
import KeyboardDoubleArrowRightIcon from "@mui/icons-material/KeyboardDoubleArrowRight";
import IconButton, { iconButtonClasses } from "@mui/joy/IconButton";
import Select from "@mui/joy/Select";
import Option from "@mui/joy/Option";
import { Box } from "@mui/joy";
import { useTableControls } from "../../hooks/useTableControls";
import { useSearchParams } from "react-router-dom";

type PaginationProps = {
  isLoading: boolean;
  isError: boolean;
  totalPages: number;
};

const Pagination = ({ isLoading, isError, totalPages }: PaginationProps) => {
  const {
    handleNextPage,
    handlePreviousPage,
    handleJumpToPage,
    reachablePages,
    setSize,
  } = useTableControls({
    isLoading: isLoading,
    isError: isError,
    totalPages: totalPages,
  });

  const [searchParams] = useSearchParams();
  const currentPage = Number(searchParams.get("page"));
  const size = Number(searchParams.get("size"));

  return (
    currentPage !== null &&
    size !== null && (
      <Box
        sx={{
          pt: 2,
          gap: 1,
          [`& .${iconButtonClasses.root}`]: { borderRadius: "50%" },
          display: "flex",
          width: "100%",
        }}
      >
        <Box sx={{ flex: 1 }} />
        <IconButton
          size="md"
          variant={"plain"}
          color="neutral"
          onClick={() => handleJumpToPage(0)}
        >
          <KeyboardDoubleArrowLeftIcon />
        </IconButton>
        <IconButton
          size="md"
          variant="plain"
          color="neutral"
          onClick={handlePreviousPage}
          disabled={currentPage === 0}
        >
          <KeyboardArrowLeftIcon />
        </IconButton>

        <Box
          display={{
            xs: "none",
            sm: "none",
            md: "flex",
          }}
          sx={{ gap: 1 }}
        >
          {reachablePages(2).map((page) => (
            <IconButton
              key={page}
              size="md"
              variant={page === currentPage ? "solid" : "outlined"}
              color="primary"
              onClick={() => handleJumpToPage(page)}
            >
              {page + 1}
            </IconButton>
          ))}
        </Box>
        <Box
          display={{
            xs: "flex",
            sm: "flex",
            md: "none",
          }}
          sx={{ gap: 1 }}
        >
          {reachablePages(0).map((page) => (
            <IconButton
              key={page}
              size="md"
              variant={page === currentPage ? "solid" : "outlined"}
              color="primary"
              onClick={() => handleJumpToPage(page)}
            >
              {page + 1}
            </IconButton>
          ))}
        </Box>

        <IconButton
          size="md"
          variant="plain"
          color="neutral"
          onClick={handleNextPage}
          disabled={currentPage === totalPages - 1}
        >
          <KeyboardArrowRightIcon />
        </IconButton>
        <IconButton
          size="md"
          variant={"plain"}
          color="neutral"
          onClick={() => handleJumpToPage(totalPages - 1)}
        >
          <KeyboardDoubleArrowRightIcon />
        </IconButton>
        <Select
          size="md"
          defaultValue={size}
          onChange={(_e, value) => {
            setSize(value!);
          }}
          variant="outlined"
        >
          {[10, 20, 50, 100].map((size) => (
            <Option key={size} value={size}>
              {size}
            </Option>
          ))}
        </Select>

        <Box sx={{ flex: 1 }} />
      </Box>
    )
  );
};

export default Pagination;
