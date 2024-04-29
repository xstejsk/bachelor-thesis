import { Box } from "@mui/joy";
import Calendar from "../components/Calendar";

const Events = () => {
  return (
    <Box
      className="main"
      // sx={{
      //   px: 2,
      //   py: 1.5,
      // }}
    >
      <Calendar />
    </Box>
  );
};

export default Events;
