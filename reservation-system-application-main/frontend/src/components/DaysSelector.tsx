import * as React from "react";
import Button from "@mui/joy/Button";
import ToggleButtonGroup from "@mui/joy/ToggleButtonGroup";

const DAYS = [
  {
    key: 1,
    label: "Po",
  },
  {
    key: 2,
    label: "Út",
  },
  {
    key: 3,
    label: "St",
  },
  {
    key: 4,
    label: "Čt",
  },
  {
    key: 5,
    label: "Pá",
  },
  {
    key: 6,
    label: "So",
  },
  {
    key: 7,
    label: "Ne",
  },
];

type ToggleGroupSpacingProps = {
  onChange: (value: string[]) => void;
};

export default function ToggleGroupSpacing(props: ToggleGroupSpacingProps) {
  const [value, setValue] = React.useState<string[]>([]); // Change the type of value to string[]

  return (
    <ToggleButtonGroup
      spacing={{ md: 2.2, sm: 2.2, xs: 0 }}
      variant="outlined"
      color="primary"
      sx={{
        width: "100%",
      }}
      value={value.map(String)} // Convert the selected values to strings
      onChange={(_event, newValue) => {
        setValue(newValue); // Ensure newValue is of type string[]
        props.onChange(newValue);
      }}
    >
      {DAYS.map((day) => (
        <Button value={String(day.key)} key={day.key}>
          {day.label}
        </Button>
      ))}
    </ToggleButtonGroup>
  );
}
