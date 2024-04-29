import * as React from "react";
import Card from "@mui/joy/Card";
import { Box } from "@mui/joy";
import { IconButton } from "@mui/joy";
import * as FaIcons from "react-icons/fa";
import FormModal from "./FormModal";
import NewLessonForm from "./forms/NewLessonForm";

export default function NewLessonCard() {
  const [open, setOpen] = React.useState(false);

  return (
    <React.Fragment>
      <Card
        variant="outlined"
        sx={{ width: 300, height: 280 }}
        className="new-calendar-card"
        onClick={() => setOpen(true)}
      >
        <Box
          display="flex"
          alignItems="center"
          justifyContent="center"
          height={"100%"}
        >
          {" "}
          <IconButton
            sx={{
              width: 56,
              height: 56,
              borderRadius: "50%",
              backgroundColor: "primary.solidBg",
              "&:hover": {
                backgroundColor: "primary.solidHoverBg", // Change the background color on hover
              },
            }}
          >
            <FaIcons.FaPlus
              style={{
                color: "white",
              }}
            />{" "}
            {/* Render the plus icon */}
          </IconButton>
        </Box>
      </Card>
      <FormModal
        open={open}
        setOpen={setOpen}
        title={"Kalendář"}
        challenge={"Vyplňte formulář pro vytvoření nového kalendáře."}
        description={undefined}
        form={<NewLessonForm withSubmit={() => setOpen(false)} />}
      />
    </React.Fragment>
  );
}
